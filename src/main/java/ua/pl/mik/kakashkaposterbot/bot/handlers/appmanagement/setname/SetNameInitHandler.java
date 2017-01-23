package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class SetNameInitHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        if (!update.getMessage().getText().equals("/setname")) {
            return false;
        }

        List<List<InlineKeyboardButton>> inlineKeyboard = Database.get().listApps(getChatId(update), getUserId(update))
                .stream()
                .map((app) -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(app.packageName);
                    button.setCallbackData("rename " + String.valueOf(app.id));
                    return button;
                })
                .map(Collections::singletonList).collect(Collectors.toList());

        if (inlineKeyboard.isEmpty()) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Немає завдань які б можна було перейменувати.");
            return true;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getChatId(update));
        sendMessage.setText("Яке завдання ви хотіли б перейменувати?");
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(inlineKeyboard));
        TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);

        return true;
    }
}
