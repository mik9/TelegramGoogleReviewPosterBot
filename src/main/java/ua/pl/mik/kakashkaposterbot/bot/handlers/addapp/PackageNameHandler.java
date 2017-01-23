package ua.pl.mik.kakashkaposterbot.bot.handlers.addapp;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.PendingApp;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class PackageNameHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        if (chat.state != ChatState.WAITONG_FOR_PACKAGE_NAME) {
            return false;
        }

        PendingApp pendingApp = Database.get().getOrCreatePendingApp(getChatId(update), getUserId(update));
        pendingApp.packageName = update.getMessage().getText();
        Database.get().savePendingApp(pendingApp);

        chat.state = ChatState.WAITING_FOR_KEY_FILE;
        Database.get().saveChat(chat);

        TelegramUtils.sendSimpleTextMessage(getChatId(update), "Відправте Json-ключ сервіс аккаунту (як отримати: https://developers.google.com/android-publisher/getting_started#setting_up_api_access_clients).");
        return true;
    }
}
