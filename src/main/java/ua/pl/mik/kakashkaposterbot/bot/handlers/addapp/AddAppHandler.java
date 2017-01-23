package ua.pl.mik.kakashkaposterbot.bot.handlers.addapp;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.bot.WrongStateException;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class AddAppHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        String message = update.getMessage().getText();
        if (!message.startsWith("/addapp")) {
            return false;
        }
        if (!update.getMessage().getChat().isUserChat()) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Зв'яжіться зі мною у приватному чаті.");
            return true;
        }
        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        if (chat.state != ChatState.NO_STATE) {
            throw new WrongStateException(chat.state);
        }
        chat.state = ChatState.WAITONG_FOR_PACKAGE_NAME;
        Database.get().saveChat(chat);

        TelegramUtils.sendSimpleTextMessage(getChatId(update), "Назвіть package name:");
        return true;
    }
}
