package ua.pl.mik.kakashkaposterbot.bot.handlers;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.Scheduler;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class StartHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        String message = update.getMessage().getText();
        if (!message.startsWith("/start")) {
            return false;
        }

        if (message.contains(" ")) {
            long pendingAppId = Long.valueOf(message.split(" ")[1]);
            App app = Database.get()
                    .createApp(Database.get().getPendingApp(pendingAppId), getChatId(update), getUserId(update));
            Scheduler.scheduleApp(app);

            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Тепер я буду постить какашки від користувачів про програму " + app.packageName);
            return true;
        }

        TelegramUtils.sendSimpleTextMessage(getChatId(update), "Я можу постить останні коментарі з Play Store про ваші програми. Почніть з /addapp");
        return true;
    }
}
