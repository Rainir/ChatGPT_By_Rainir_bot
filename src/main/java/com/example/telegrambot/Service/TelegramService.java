package com.example.telegrambot.Service;

import com.example.telegrambot.Model.Telegram;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

@Service
public class TelegramService extends TelegramLongPollingBot {

    private final Telegram telegram;

    public TelegramService(Telegram telegram) {
        this.telegram = telegram;
    }

    @Bean
    public void botStart() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(this);
        System.out.println("Бот запущен и работает!");
    }

    @Override
    public String getBotUsername() {
        return telegram.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return telegram.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String userMessage = update.getMessage().getText();
            String response;
            if (handleCommand(userMessage) != null) {
                response = handleCommand(userMessage);
            } else {
                try {
                    response = ChatGptService.getResponseFromChatGPT(userMessage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            sendMessageToUser(chatId, response);
        }
    }

    private void sendMessageToUser(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String handleCommand(String userMessage) {
        String command = null;
        switch (userMessage) {
            case "/start" -> command = "Привет! Я бот, который может общаться с ChatGPT.";
            case "/faq" -> command = "Это ChatGPT версии 3.5\n" + "Не отправляйте сообщения начиная с '/'";
            case "/help" -> command = "Это раздел помощи.";
        }
        return command;
    }
}
