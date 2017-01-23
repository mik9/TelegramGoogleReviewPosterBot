package ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.delete;

import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.bot.handlers.BaseTextMessageHandler;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.isUserAdmin;

public class DeleteHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        if (!update.getMessage().getText().startsWith("/delete")) {
            return false;
        }

        GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
        getChatAdministrators.setChatId(getChatId(update));
        List<ChatMember> chatAdministrators = TelegramBotImpl.telegramAbsSender.getChatAdministrators(getChatAdministrators);

        Set<App> appSet;
        if (isUserAdmin(chatAdministrators, update.getMessage().getFrom())) {
            appSet = Database.get().listApps(getChatId(update));
        } else {
            appSet = Database.get().listApps(getChatId(update), getUserId(update));
        }

        List<List<InlineKeyboardButton>> buttons = appSet.stream()
                .map(app -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(app.getName());
                    button.setCallbackData("delete " + String.valueOf(app.id));
                    return button;
                })
                .map(Collections::singletonList)
                .collect(Collectors.toList());

        if (buttons.isEmpty()) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Немає завдань які ви можете видалити.");
            return true;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(getChatId(update));
        sendMessage.setText("Яке завдання ви хотіли б видалити?");
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(buttons));

        TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);

        return true;
    }
}
