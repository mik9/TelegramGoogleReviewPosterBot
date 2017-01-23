package ua.pl.mik.kakashkaposterbot.db;

import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.PendingApp;

import java.util.Set;

public interface IDatabase {
    default void init() {
    }

    Chat getOrCreateChat(long chatId, long userId);
    void saveChat(Chat chat);
    void deleteChat(Chat chat);

    PendingApp getOrCreatePendingApp(long chatId, long userId);
    PendingApp getPendingApp(long pendingAppId);
    void savePendingApp(PendingApp pendingApp);
    void deletePendingApp(PendingApp pendingApp);

    App getApp(long id);
    App createApp(PendingApp pendingApp, long targetChatId, long targetUserId);
    void saveApp(App app);
    void deleteApp(App app);
    Set<App> listApps();
    Set<App> listApps(long chatId);
    Set<App> listApps(long chatId, long userId);
}
