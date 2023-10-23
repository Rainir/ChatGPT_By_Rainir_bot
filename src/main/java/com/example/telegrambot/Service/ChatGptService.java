package com.example.telegrambot.Service;

import com.example.telegrambot.Model.ChatGpt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {

    private static ChatGpt chatGpt;
    private static RestTemplate restTemplate;

    @Autowired
    ChatGptService(ChatGpt chatGpt, RestTemplate restTemplate) {
        ChatGptService.chatGpt = chatGpt;
        ChatGptService.restTemplate = restTemplate;
    }

    public static String getResponseFromChatGPT(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(chatGpt.getChatGptApiKey());

        String postData = "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"" + userMessage + "\"}]}";

        HttpEntity<String> requestEntity = new HttpEntity<>(postData, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(chatGpt.getChatGptApiUrl(), requestEntity, String.class);

        return parseResponse(responseEntity.getBody());
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
            throw new RuntimeException("Ошибка парсинга ответа.", e);
        }
        return "Ошибка при обработке ответа от ChatGPT.";
    }
}