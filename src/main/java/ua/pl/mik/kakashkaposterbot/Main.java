package ua.pl.mik.kakashkaposterbot;

import ua.pl.mik.kakashkaposterbot.bot.TelegramBotImpl;
import ua.pl.mik.kakashkaposterbot.db.Database;

public class Main {
    public static void main(String[] args) throws Exception {
        GoogleApi.init();
        Database.init();
        Scheduler.init();
        TelegramBotImpl.init();
    }
}
