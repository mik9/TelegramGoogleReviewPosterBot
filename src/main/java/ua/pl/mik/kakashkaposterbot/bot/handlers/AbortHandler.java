package ua.pl.mik.kakashkaposterbot.bot.handlers;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.PendingApp;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.sendSimpleTextMessage;

public class AbortHandler extends BaseTextMessageHandler {

    public static final String NOTHING_TO_ABORT = "Нічого відміняти.";
    public static final String PENDING_APPS_DELETED = "Підготовлювані програми видалені.";

    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        String message = update.getMessage().getText();
        if (!message.startsWith("/abort")) {
            return false;
        }

        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        switch (chat.state) {
            case NO_STATE:
                sendSimpleTextMessage(getChatId(update), NOTHING_TO_ABORT);
                return true;
            case WAITONG_FOR_PACKAGE_NAME: {
                PendingApp pendingApp = Database.get().getOrCreatePendingApp(getChatId(update), getUserId(update));
                Database.get().deletePendingApp(pendingApp);
                sendSimpleTextMessage(getChatId(update), PENDING_APPS_DELETED);
                break;
            }
            case WAITING_FOR_KEY_FILE: {
                PendingApp pendingApp = Database.get().getOrCreatePendingApp(getChatId(update), getUserId(update));
                Database.get().deletePendingApp(pendingApp);
                sendSimpleTextMessage(getChatId(update), PENDING_APPS_DELETED);
                break;
            }
            case WAITING_FOR_APP_TO_STOP: {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Зупинка програми відмінена.");
                sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
                sendMessage.setChatId(getChatId(update));
                TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);
                break;
            }
        }

        chat.clearState();
        Database.get().saveChat(chat);
        return true;
    }
}
