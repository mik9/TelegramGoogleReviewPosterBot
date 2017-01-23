package ua.pl.mik.kakashkaposterbot.bot.handlers;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.Handler;

public abstract class BaseCallbackQueryHandler implements Handler {
    @Override
    public final boolean handle(Update update) throws TelegramApiException {
        if (update.getCallbackQuery() != null && update.getCallbackQuery().getData() != null) {
            return handleCallbackQuery(update);
        }
        return false;
    }

    protected abstract boolean handleCallbackQuery(Update update) throws TelegramApiException;
}
