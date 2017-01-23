package ua.pl.mik.kakashkaposterbot.utils;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TelegramUtils {
    public static long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    public static long getUserId(Update update) {
        return update.getMessage().getFrom().getId();
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

    public static File generateKeyFile() {
        return new File("data/" + UUID.randomUUID().toString());
    }

    public static String androidApiVersionToString(int apiVersion) {
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
