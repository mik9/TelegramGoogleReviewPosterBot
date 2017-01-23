package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changelanguage;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.Locale;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class ChangeLanguageName extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        if (chat.state != ChatState.WAITING_FOR_LANGUAGE) {
            return false;
        }

        boolean haveMatch = false;
        String newLanguage = update.getMessage().getText();
        for (String s : Locale.getISOLanguages()) {
            if (s.equals(newLanguage)) {
                haveMatch = true;
                break;
            }
        }

        if (!haveMatch) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update),
                    "Невідомий код, уведіть один із списку https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes.\n" +
                            "Увага, не всі коди підримуються Google Translate.");
            return true;
        }

        App app = Database.get().getApp(Long.valueOf(chat.customStateData));
        app.translateLanguage = newLanguage;
        Database.get().saveApp(app);

        chat.clearState();
        Database.get().saveChat(chat);

        TelegramUtils.sendSimpleTextMessage(getChatId(update), "Змінено!");

        return true;
    }
}
