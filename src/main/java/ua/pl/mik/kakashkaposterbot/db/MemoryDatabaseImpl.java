package ua.pl.mik.kakashkaposterbot.db;

import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.PendingApp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class MemoryDatabaseImpl implements IDatabase {
    private static final Set<Chat> CHAT_STORAGE = new HashSet<>();
    private static final Set<PendingApp> PENDING_APP_STORAGE = new HashSet<>();
    private static final Set<App> APP_STORAGE = new HashSet<>();

    @Override
    public Chat getOrCreateChat(long chatId, long userId) {
        Optional<Chat> chatOptional = CHAT_STORAGE.stream()
                .filter(chat -> chat.chatId == chatId && chat.userId == userId)
                .findFirst();

        if (chatOptional.isPresent()) {
            return chatOptional.get();
        }

        Chat chat = new Chat();

        chat.chatId = chatId;
        chat.userId = userId;
        return chat;
    }

    @Override
    public void saveChat(Chat chat) {
        CHAT_STORAGE.add(chat);
    }

    @Override
    public void deleteChat(Chat chat) {
        CHAT_STORAGE.remove(chat);
    }

    @Override
    public PendingApp getOrCreatePendingApp(long chatId, long userId) {
        Optional<PendingApp> pendingAppOptional = PENDING_APP_STORAGE.stream()
                .filter(pendingApp -> pendingApp.chatId == chatId && pendingApp.userId == userId)
                .findFirst();
        if (pendingAppOptional.isPresent()) {
            return pendingAppOptional.get();
        }

        return new PendingApp(chatId, userId);
    }

    @Override
    public PendingApp getPendingApp(long pendingAppId) {
        return PENDING_APP_STORAGE.stream().filter(pendingApp -> pendingApp.pendingAppId == pendingAppId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void savePendingApp(PendingApp pendingApp) {
        PENDING_APP_STORAGE.add(pendingApp);
    }

    @Override
    public void deletePendingApp(PendingApp pendingApp) {
        PENDING_APP_STORAGE.remove(pendingApp);
    }

    @Override
    public App getApp(long id) {
        Optional<App> appOptional = APP_STORAGE.stream()
                .filter(app -> app.id == id)
                .findFirst();

        return appOptional.orElseGet(() -> null);
    }

    @Override
    public App createApp(PendingApp pendingApp, long targetChatId, long targetUserId) {
        App app = new App();
        app.packageName = pendingApp.packageName;
        app.chatId = targetChatId;
        app.userId = targetUserId;
        app.keyFilePath = pendingApp.keyFilePath;
        app.enabled = true;

        saveApp(app);

        return app;
    }

    @Override
    public void saveApp(App app) {
        APP_STORAGE.add(app);
    }

    @Override
    public void deleteApp(App app) {
        APP_STORAGE.remove(app);
    }

    @Override
    public Set<App> listAppsByChatId() {
        return Collections.emptySet();
    }

    @Override
    public Set<App> listAppsByChatId(long chatId) {
        return APP_STORAGE.stream().filter(app -> app.chatId == chatId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<App> listAppsByChatId(long chatId, long userId) {
        return APP_STORAGE.stream().filter(app -> app.chatId == chatId && app.userId == userId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<App> listAppsByUserId(long userId) {
        return APP_STORAGE.stream().filter(app -> app.userId == userId)
                .collect(Collectors.toSet());
    }
}
