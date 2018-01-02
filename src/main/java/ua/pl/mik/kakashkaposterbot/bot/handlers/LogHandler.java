package ua.pl.mik.kakashkaposterbot.bot.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.Handler;

public class LogHandler implements Handler {
    private final static Logger logger = LoggerFactory.getLogger(LogHandler.class);

    @Override
    public boolean handle(Update update) throws TelegramApiException {
        logger.debug(update.toString());
        return false;
    }
}
