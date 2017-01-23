package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.WrongStateException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.List;
import java.util.stream.Collectors;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class DeactivateHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        if (!update.getMessage().getText().startsWith("/deactivate")) {
            return false;
        }

        Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
        if (chat.state != ChatState.NO_STATE) {
            throw new WrongStateException(chat.state);
        }

        List<KeyboardRow> keyboardRows = Database.get().listApps(getChatId(update), getUserId(update))
                .stream()
                .filter(app -> app.enabled)
                .map(app -> app.packageName)
                .map(s -> {
                    KeyboardRow keyboardRow = new KeyboardRow();
                    keyboardRow.add(new KeyboardButton(s));
                    return keyboardRow;
                }).collect(Collectors.toList());

        if (keyboardRows.isEmpty()) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Немає завдань, що можна деактивувати.");
            return true;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(new ReplyKeyboardMarkup()
                .setKeyboard(keyboardRows)
                .setOneTimeKeyboad(true));
        sendMessage.setChatId(getChatId(update));
        sendMessage.setText("Яке з завдань ви хочете деактивувати?");

        chat.state = ChatState.WAITING_FOR_APP_TO_STOP;
        Database.get().saveChat(chat);
        TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);


        return true;
    }
}
