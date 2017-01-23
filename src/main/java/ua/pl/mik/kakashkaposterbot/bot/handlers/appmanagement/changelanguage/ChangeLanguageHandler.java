package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changelanguage;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.List;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;

public class ChangeLanguageHandler extends BaseTextMessageHandler {

    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        if (!update.getMessage().getText().startsWith("/changelanguage")) {
            return false;
        }

        List<List<InlineKeyboardButton>> buttons =
                TelegramUtils.createBotInlineSelectionKeyboard(update, "changelanguage");

        if (buttons.isEmpty()) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Немає завдань для яких можна змінити мову.");
            return true;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getChatId(update));
        sendMessage.setText("В якому завданні ви хотіли б змінити мову?");
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(buttons));

        TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);

        return true;
    }
}
