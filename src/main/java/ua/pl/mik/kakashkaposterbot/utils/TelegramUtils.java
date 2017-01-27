package ua.pl.mik.kakashkaposterbot.utils;

import org.telegram.telegrambots.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TelegramUtils {
    public static long getChatId(Update update) {
        if (update.getMessage() != null) {
            return getChatId(update.getMessage());
        } else if (update.getCallbackQuery().getMessage() != null) {
            return getChatId(update.getCallbackQuery().getMessage());
        }
        throw new RuntimeException();
    }

    public static long getUserId(Update update) {
        if (update.getMessage() != null) {
            return getUserId(update.getMessage());
        } else if (update.getCallbackQuery().getMessage() != null) {
            return getUserId(update.getCallbackQuery().getMessage());
        }
        throw new RuntimeException();
    }

    public static long getChatId(Message message) {
        return message.getChatId();
    }

    public static long getUserId(Message message) {
        return message.getFrom().getId();
    }

    public static void sendSimpleTextMessage(long chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        TelegramBotImpl.telegramAbsSender.sendMessage(sendMessage);
    }

    public static boolean isUserAdmin(List<ChatMember> chatAdmins, User user) {
        return chatAdmins.stream().anyMatch(chatMember -> Objects.equals(chatMember.getUser().getId(), user.getId()));
    }

    public static boolean isUserAdmin(User user, Chat chat) {
        GetChatAdministrators ar = new GetChatAdministrators();
        ar.setChatId(chat.getId());

        try {
            return isUserAdmin(TelegramBotImpl.telegramAbsSender.getChatAdministrators(ar), user);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Set<App> getAppsForChat(Chat chat, User user) {
        Set<App> apps;
        if (chat.isUserChat()) {
            apps = Database.get().listAppsByUserId(user.getId());
        } else if (isUserAdmin(user, chat)) {
            apps = Database.get().listAppsByChatId(chat.getId());
        } else {
            apps = Database.get().listAppsByChatId(chat.getId(), user.getId());
        }
        return apps;
    }

    public static boolean isManagementAllowed(Chat chat, User user, App app) {
        if (app.userId == user.getId() && app.chatId == chat.getId()) {
            return true;
        }
        if (app.chatId == chat.getId() && isUserAdmin(user, chat)) {
            return true;
        }
        if (app.userId == user.getId() && chat.isUserChat()) {
            return true;
        }
        return false;
    }

    public static List<List<InlineKeyboardButton>> createBotInlineSelectionKeyboard(Update update, String command)
            throws TelegramApiException {
        Set<App> appSet = getAppsForChat(update.getMessage().getChat(), update.getMessage().getFrom());
        return appSet.stream()
                .map(app -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(app.getName());
                    button.setCallbackData(command + " " + String.valueOf(app.id));
                    return button;
                })
                .map(Collections::singletonList)
                .collect(Collectors.toList());
    }

    public static File generateKeyFile() {
        return new File("data/" + UUID.randomUUID().toString());
    }

    public static String androidApiVersionToString(@Nullable Integer apiVersion) {
        if (apiVersion == null) {
            return null;
        }
        switch (apiVersion) {
            case 25:
                return "7.1";
            case 24:
                return "7.0";
            case 23:
                return "6.0";
            case 22:
                return "5.1";
            case 21:
                return "5.0";
            case 20:
                return "4.4W o_O";
            case 19:
                return "4.4";
            case 18:
                return "4.3";
            case 17:
                return "4.2";
            case 16:
                return "4.1";
            case 15:
                return "4.0.4";
            case 14:
                return "4.0";
            default:
                return "unknown";
        }
    }
}
