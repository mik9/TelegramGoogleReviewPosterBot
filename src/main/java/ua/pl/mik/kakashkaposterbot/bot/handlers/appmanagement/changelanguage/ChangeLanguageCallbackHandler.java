package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changelanguage;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.WrongStateException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseCallbackQueryHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;

public class ChangeLanguageCallbackHandler extends BaseCallbackQueryHandler {
    @Override
    protected boolean handleCallbackQuery(Update update) throws TelegramApiException {
        String callback = update.getCallbackQuery().getData();
        if (!callback.startsWith("changelanguage")) {
            return false;
        }

        Integer userId = update.getCallbackQuery().getFrom().getId();
        Chat chat = Database.get().getOrCreateChat(getChatId(update.getCallbackQuery().getMessage()),
                userId);
        if (chat.state != ChatState.NO_STATE) {
            throw new WrongStateException(chat.state);
        }

        long appId = Long.valueOf(callback.split(" ")[1]);
        App app = Database.get().getApp(appId);
        if (app == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setText("Невідома задача, може вона вже видалена?");

            TelegramBotImpl.telegramAbsSender.answerCallbackQuery(answerCallbackQuery);
            return true;
        }
        if (!TelegramUtils.isManagementAllowed(update.getCallbackQuery().getMessage().getChat(), update.getCallbackQuery().getFrom(), app)) {
            return true;
        }

        chat.state = ChatState.WAITING_FOR_LANGUAGE;
        chat.customStateData = String.valueOf(app.id);

        Database.get().saveChat(chat);

        TelegramUtils.sendSimpleTextMessage(getChatId(update.getCallbackQuery().getMessage()),
                "Уведіть двобуквенний код (див. https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes).\n" +
                        "Увага, не всі коди підримуються Google Translate.");

        return true;
    }
}
