package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseCallbackQueryHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class SetNameAppChosenHandler extends BaseCallbackQueryHandler {

    @Override
    protected boolean handleCallbackQuery(Update update) throws TelegramApiException {
        String callback = update.getCallbackQuery().getData();
        if (!callback.startsWith("rename")) {
            return false;
        }

        String appId = callback.split(" ")[1];

        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        chat.state = ChatState.WAITING_FOR_APP_NAME;
        chat.customStateData = appId;
        Database.get().saveChat(chat);

        return true;
    }
}
