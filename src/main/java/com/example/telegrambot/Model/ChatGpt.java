package com.example.telegrambot.Model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChatGpt {

    @Value("${chatgpt.api.key}")
    private String API_KEY;
    @Value("${chatgpt.api.url}")
    private String API_URL;

    public ChatGpt() {}

    public String getChatGptApiKey() {
        return API_KEY;
    }

    public String getChatGptApiUrl() {
        return API_URL;
    }
}