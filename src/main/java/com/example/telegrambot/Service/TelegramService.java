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
            String chatId = update.getMessage().getChatId().toString();
            String userMessage = update.getMessage().getText();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            if (userMessage.startsWith("/")) {
                handleCommand(userMessage, sendMessage);
            } else {
                sendMessage.setText(ChatGptService.getResponseFromChatGPT(userMessage));
            }

            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException("Ошибка при отправке сообщения.", e);
            }
        }
    }

    private void handleCommand(String userMessage, SendMessage sendMessage) {
        switch (userMessage) {
            case "/start" -> sendMessage.setText("Привет! Я бот, который может общаться с ChatGPT.");
            case "/faq" -> sendMessage.setText("Это ChatGPT версии 3.5\n" + "Не отправляйте сообщения начиная с '/'");
            case "/help" -> sendMessage.setText("Это раздел помощи.");
            default -> sendMessage.setText("Неизвестная команда.");
        }
    }
}