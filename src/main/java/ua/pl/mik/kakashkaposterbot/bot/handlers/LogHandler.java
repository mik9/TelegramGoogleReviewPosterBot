package ua.pl.mik.kakashkaposterbot.bot.handlers;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.Handler;

public class LogHandler implements Handler {
    @Override
    public boolean handle(Update update) throws TelegramApiException {
        System.out.println(update.toString());
        return false;
    }
}
