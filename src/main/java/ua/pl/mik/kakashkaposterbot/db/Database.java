package ua.pl.mik.kakashkaposterbot.db;

public class Database {
    private static IDatabase database;

    public static void init() {
        database = new OrmLiteSqliteDatabaseImpl();

        database.init();
    }

    public static IDatabase get() {
        return database;
    }
}
