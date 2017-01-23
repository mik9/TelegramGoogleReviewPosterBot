package ua.pl.mik.kakashkaposterbot.db;

public class Database {
    private static IDatabase database;

    public static void init() {
        database = new OrmliteSqliteDatabaseImpl();

        database.init();
    }

    public static IDatabase get() {
        return database;
    }
}
