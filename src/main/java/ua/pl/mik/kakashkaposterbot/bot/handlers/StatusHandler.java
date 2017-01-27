package ua.pl.mik.kakashkaposterbot.bot.handlers;

import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.util.Set;
import java.util.stream.Collectors;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;

public class StatusHandler extends BaseTextMessageHandler {
    @Override
    protected boolean handleTextMessage(Update update) throws TelegramApiException {
        if (!update.getMessage().getText().startsWith("/status")) {
            return false;
        }

        Set<App> apps = TelegramUtils.getAppsForChat(update.getMessage().getChat(), update.getMessage().getFrom());

        Set<String> strings = apps.stream()
                .map(App::getName)
                .collect(Collectors.toSet());

        if (strings.isEmpty()) {
            TelegramUtils.sendSimpleTextMessage(getChatId(update), "Немає налаштованих завдань в цьому чаті.");
        } else {
            String resp = "Я буду постить коментарі до наступних програм:\n" + String.join("\n", strings);
            TelegramUtils.sendSimpleTextMessage(getChatId(update), resp);
        }

        return true;
    }
}
