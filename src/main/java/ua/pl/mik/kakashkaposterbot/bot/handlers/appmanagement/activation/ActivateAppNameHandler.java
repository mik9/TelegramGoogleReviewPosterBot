package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.Scheduler;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseCallbackQueryHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.Optional;
import java.util.Set;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class ActivateAppNameHandler extends BaseCallbackQueryHandler {

    @Override
    protected boolean handleCallbackQuery(Update update) throws TelegramApiException {
        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        if (chat.state != ChatState.WAITING_FOR_APP_TO_START) {
            return false;
        }
        String userText = update.getCallbackQuery().getData();

//        if (!TelegramUtils.isManagementAllowed(update.getCallbackQuery().getMessage().getChat(),
//                update.getCallbackQuery().getMessage().getFrom())) {
//
//        }

        Set<App> appSet;
        if (update.getCallbackQuery().getMessage().getChat().isUserChat()) {
            appSet = Database.get().listAppsByUserId(getUserId(update));
        } else {
            appSet = Database.get().listAppsByChatId(getChatId(update), getUserId(update));
        }

        Optional<App> appOptional = appSet
                .stream()
                .filter(app -> app.id == Long.parseLong(userText.split("\\s")[1]))
                .findAny();

        if (appOptional.isPresent()) {
            App app = appOptional.get();
            app.enabled = true;
            Database.get().saveApp(app);
            Scheduler.scheduleApp(app);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
            sendMessage.setChatId(getChatId(update));
            sendMessage.setText("Завдання увімкнено.");

            TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);
        } else {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Не знаю такої програми: \"" + userText + "\"");
        }

        return true;
    }
}
