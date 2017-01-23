package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class SetNameHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        if (chat.state != ChatState.WAITING_FOR_APP_NAME) {
            return false;
        }

        App app = Database.get().getApp(Long.valueOf(chat.customStateData));
        app.name = update.getMessage().getText();
        Database.get().saveApp(app);

        chat.clearState();
        Database.get().saveChat(chat);

        return true;
    }
}
