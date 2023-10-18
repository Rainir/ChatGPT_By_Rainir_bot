package com.example.telegrambot.Service;

import com.example.telegrambot.Model.ChatGpt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class ChatGptService {

    private static ChatGpt chatGpt;
    public ChatGptService(ChatGpt chatGpt) {
        ChatGptService.chatGpt = chatGpt;
    }

    public static String getResponseFromChatGPT(String userMessage) throws IOException {
        final URL url = new URL(chatGpt.getChatGptApiUrl());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + chatGpt.getChatGptApiKey());
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
            if (!choices.isEmpty()) {
                JSONObject chatGptResponse = choices.getJSONObject(0);
                JSONObject message = chatGptResponse.getJSONObject("message");
                return message.getString("content").trim();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Ошибка при обработке ответа от ChatGPT.";
    }
}