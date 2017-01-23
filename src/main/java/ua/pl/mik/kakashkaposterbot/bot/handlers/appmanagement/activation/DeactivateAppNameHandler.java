package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.Scheduler;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.Optional;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class DeactivateAppNameHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        if (chat.state != ChatState.WAITING_FOR_APP_TO_STOP) {
            return false;
        }
        String userText = update.getMessage().getText();

        Optional<App> appOptional = Database.get().listApps(getChatId(update), getUserId(update))
                .stream().filter(app -> app.packageName.equals(userText))
                .findAny();

        if (appOptional.isPresent()) {
            App app = appOptional.get();
            app.enabled = false;
            Database.get().saveApp(app);
            Scheduler.unSchedule(app);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
            sendMessage.setChatId(getChatId(update));
            sendMessage.setText("Завдання вимкнено але не видалено.");

            TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);
        } else {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Не знаю такої програми: \"" + userText + "\"");
        }

        return true;
    }
}
