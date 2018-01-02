package ua.pl.mik.kakashkaposterbot.bot.handlers.addapp;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.Handler;
import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.ChatState;
import ua.pl.mik.kakashkaposterbot.db.models.PendingApp;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.io.IOException;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getChatId;
import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.getUserId;

public class KeyFileHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(KeyFileHandler.class);

    public static final String READY_MESSAGE = "Завдання готове. Ви можете додати мене у групу за наступним посиланням:\n" +
            "https://telegram.me/kakashkaposterbot?startgroup=%d";

    @Override
    public boolean handle(Update update) throws TelegramApiException {
        if (update.getMessage() != null && update.getMessage().getDocument() != null &&
                update.getMessage().getDocument().getFileId() != null) {
            Chat chat = Database.get().getOrCreateChat(getChatId(update), getUserId(update));
            if (chat.state != ChatState.WAITING_FOR_KEY_FILE) {
                return false;
            }

            GetFile getFile = new GetFile();
            getFile.setFileId(update.getMessage().getDocument().getFileId());
            File file = TelegramBotImpl.telegramAbsSender.getFile(getFile);
            java.io.File downloadedFile = TelegramBotImpl.telegramAbsSender.downloadFile(file.getFilePath());
            java.io.File keyFile;
            try {
                keyFile = TelegramUtils.generateKeyFile();
                Files.move(downloadedFile, keyFile);
            } catch (IOException e) {
                throw new RuntimeException("Can't move file.", e);
            }
            logger.debug(keyFile.getAbsolutePath());

            chat.state = ChatState.NO_STATE;
            Database.get().saveChat(chat);

            PendingApp pendingApp = Database.get().getOrCreatePendingApp(getChatId(update), getUserId(update));
            pendingApp.keyFilePath = keyFile.getAbsolutePath();
            Database.get().savePendingApp(pendingApp);

            TelegramUtils.sendSimpleTextMessage(getChatId(update), String.format(READY_MESSAGE, pendingApp.pendingAppId));

            return true;
        }

        return false;
    }
}
