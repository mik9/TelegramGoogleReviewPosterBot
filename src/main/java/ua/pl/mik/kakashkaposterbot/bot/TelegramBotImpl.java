package ua.pl.mik.kakashkaposterbot.bot;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ua.pl.mik.kakashkaposterbot.bot.handlers.AbortHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.LogHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.StartHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.StatusHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.addapp.AddAppHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.addapp.KeyFileHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.addapp.PackageNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation.ActivateAppNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation.ActivateHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation.DeactivateAppNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.activation.DeactivateHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changechat.ChangeChatAppNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changechat.ChangeChatHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changelanguage.ChangeLanguageCallbackHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changelanguage.ChangeLanguageHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.changelanguage.ChangeLanguageName;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.delete.DeleteCallbackHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.delete.DeleteHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname.SetNameAppChosenHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname.SetNameHandler;
import ua.pl.mik.kakashkaposterbot.bot.handlers.appmanagement.setname.SetNameInitHandler;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class TelegramBotImpl extends TelegramLongPollingBot {

    public static final String TOKEN;
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotImpl.class);

    static {
        try {
            TOKEN = Files.readFirstLine(new File("TELEGRAM_TOKEN"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("File TELEGRAM_TOKEN not found. Please get bot token from http://telegram.me/botfather and save it as TELEGRAM_TOKEN file.", e);
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
            new ActivateHandler(),
            new ActivateAppNameHandler(),
            new ChangeChatHandler(),
            new ChangeChatAppNameHandler(),

            // Set name flow
            new SetNameInitHandler(),
            new SetNameAppChosenHandler(),
            new SetNameHandler(),

            // Delete flow
            new DeleteHandler(),
            new DeleteCallbackHandler(),

            // Change language
            new ChangeLanguageHandler(),
            new ChangeLanguageCallbackHandler(),
            new ChangeLanguageName(),
    };

    @Override
    public void onUpdateReceived(Update update) {
        boolean handled = false;
        for (Handler handler : handlers) {
            try {
                if (handler.handle(update)) {
                    handled = true;
                    logger.debug("Handled by {}", handler.getClass().getName());
                    break;
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
                break;
            } catch (BotAnswerException e) {
                SendMessage resp = new SendMessage();
                resp.setChatId(TelegramUtils.getChatId(update));
                resp.setText(e.getMessage());
                try {
                    sendMessage(resp);
                } catch (TelegramApiException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (!handled) {
            logger.debug("Message not handled by anyone");
        }
    }

    @Override
    public String getBotUsername() {
        return "Google Play Review Bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
