package ua.pl.mik.kakashkaposterbot.bot;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public interface Handler {
    boolean handle(Update update) throws TelegramApiException;
}
