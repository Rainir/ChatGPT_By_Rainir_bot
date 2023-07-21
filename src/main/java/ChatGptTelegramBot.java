import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatGptTelegramBot extends TelegramLongPollingBot {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    // Здесь укажи свой api key от ChatGPT
    private static final String API_KEY = "YOUR_CHATGPT_API_KEY";

    // Здесь укажи свой токен Telegram бота
    private final String BOT_TOKEN = "YOUR_BOT_TOKEN";

    // Метод для отправки сообщения
    private void sendMessageToUser(long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId)); // Преобразуем chatId в строку
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String userMessage = update.getMessage().getText();

            // Здесь вызываем функцию для общения с ChatGPT и получения ответа
            String response = null;
            try {
                response = getResponseFromChatGPT(userMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Отправляем ответ пользователю
            sendMessageToUser(chatId, response);
        }
    }

    // Здесь реализуй метод для общения с ChatGPT по API
    private String getResponseFromChatGPT(String userMessage) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + API_KEY);
        con.setRequestProperty("Content-Type", "application/json");

        String postData = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"" + userMessage + "\"}]}";

        con.setDoOutput(true);
        con.getOutputStream().write(postData.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return parseResponse(response.toString());
    }

    private static String parseResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject chatGptResponse = choices.getJSONObject(0);
                JSONObject message = chatGptResponse.getJSONObject("message");
                return message.getString("content").trim();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Ошибка при обработке ответа от ChatGPT.";
    }

    @Override
    public String getBotUsername() {
        return "Chat_Name_Telegram_Bot"; // Укажи имя своего бота
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    public static void main(String[] args) throws TelegramApiException {
        ChatGptTelegramBot bot = new ChatGptTelegramBot();
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            botsApi.registerBot(bot);
            System.out.println("Bot is up and running!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
