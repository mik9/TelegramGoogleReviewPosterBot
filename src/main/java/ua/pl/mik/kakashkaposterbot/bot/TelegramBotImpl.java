package ua.pl.mik.kakashkaposterbot.bot;

import com.google.common.io.Files;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.AbortHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.addapp.AddAppHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.addapp.KeyFileHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.LogHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.addapp.PackageNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.delete.DeleteCallbackHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.delete.DeleteHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname.SetNameAppChosenHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname.SetNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname.SetNameInitHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.StartHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.StatusHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation.DeactivateAppNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation.DeactivateHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class TelegramBotImpl extends TelegramLongPollingBot {

    public static final String TOKEN;
    static {
        try {
            TOKEN = Files.readFirstLine(new File("TELEGRAM_TOKEN"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static DefaultAbsSender telegramAbsSender;

    public static void init() {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        TelegramBotImpl bot = new TelegramBotImpl();
        try {
            api.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            throw new RuntimeException(e);
        }
        telegramAbsSender = bot;
    }

    Handler[] handlers = {
            new LogHandler(),

            new AbortHandler(),

            // Add app flow
            new AddAppHandler(),
            new PackageNameHandler(),
            new KeyFileHandler(),

            new StartHandler(),
            new StatusHandler(),
            new DeactivateHandler(),
            new DeactivateAppNameHandler(),

            // Set name flow
            new SetNameInitHandler(),
            new SetNameAppChosenHandler(),
            new SetNameHandler(),

            // Delete flow
            new DeleteHandler(),
            new DeleteCallbackHandler()
    };

    @Override
    public void onUpdateReceived(Update update) {
        boolean handled = false;
        for (Handler handler : handlers) {
            try {
                if (handler.handle(update)) {
                    handled = true;
                    System.out.println("Handled by " + handler.getClass().getName());
                    break;
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
                break;
            } catch (BotAnswerException e) {
                SendMessage resp = new SendMessage();
                resp.setChatId(update.getMessage().getChatId());
                resp.setText(e.getMessage());
                try {
                    sendMessage(resp);
                } catch (TelegramApiException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (!handled) {
            System.out.println("Message not handler by anyone");
        }
    }

    @Override
    public String getBotUsername() {
        return "Kakashka";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
