package com.example.telegrambot.Model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Telegram {

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;

    @Value("${telegram.bot.name}")
    private String BOT_NAME;

    public Telegram() {}

    public String getBotToken() {
        return BOT_TOKEN;
    }

    public String getBotUsername() {
        return BOT_NAME;
    }
}