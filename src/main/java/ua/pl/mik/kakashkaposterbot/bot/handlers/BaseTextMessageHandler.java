package ua.pl.mik.kakashkaposterbot.bot.handlers;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.Handler;

public abstract class BaseTextMessageHandler implements Handler {
    @Override
    public final boolean handle(Update update) throws TelegramApiException {
        if (update.getMessage() != null && update.getMessage().getText() != null) {
            return handleTextMessage(update);
        }
        return false;
    }

    protected abstract boolean handleTextMessage(Update update) throws TelegramApiException;
}
