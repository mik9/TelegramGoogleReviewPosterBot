package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.delete;

import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.Scheduler;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseCallbackQueryHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DeleteCallbackHandler extends BaseCallbackQueryHandler {
    @Override
    protected boolean handleCallbackQuery(Update update) throws TelegramApiException {
        String callback = update.getCallbackQuery().getData();
        if (!callback.startsWith("delete")) {
            return false;
        }

        long appId = Long.valueOf(callback.split(" ")[1]);
        App app = Database.get().getApp(appId);
        if (app == null) {
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
            answerCallbackQuery.setText("Невідома задача, може вона вже видалена?");

            TelegramBotImpl.telegramAbsSender.answerCallbackQuery(answerCallbackQuery);
            return false;
        }
        try {
            Files.deleteIfExists(new File(app.keyFilePath).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Database.get().deleteApp(app);
        Scheduler.unSchedule(app);

        TelegramUtils.sendSimpleTextMessage(update.getCallbackQuery().getMessage().getChatId(),
                "Завдання " + app.getName() + " видалено.");

        return false;
    }
}
