package ua.pl.mik.kakashkaposterbot.db;

import com.google.common.io.Files;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.db.models.Chat;
import ua.pl.mik.kakashkaposterbot.db.models.PendingApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

public class OrmLiteSqliteDatabaseImpl implements IDatabase {
    public static final int DB_VERSION = 3;
    public static final String DB_VERSION_FILE_NAME = "data/db_version";

    private RuntimeExceptionDao<Chat, Long> chatDao;
    private RuntimeExceptionDao<PendingApp, Long> pendingAppsDao;
    private RuntimeExceptionDao<App, Long> appsDao;

    @Override
    public void init() {
        // this uses h2 but you can change it to match your database
        String databaseUrl = "jdbc:sqlite:data/test.db";
        // create a connection source to our database
        ConnectionSource connectionSource;
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
            chatDao = RuntimeExceptionDao.createDao(connectionSource, Chat.class);
            pendingAppsDao = RuntimeExceptionDao.createDao(connectionSource, PendingApp.class);
            appsDao = RuntimeExceptionDao.createDao(connectionSource, App.class);

            TableUtils.createTableIfNotExists(connectionSource, Chat.class);
            TableUtils.createTableIfNotExists(connectionSource, PendingApp.class);
            TableUtils.createTableIfNotExists(connectionSource, App.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int version;
        try {
            version = Integer.valueOf(Files.readFirstLine(new File(DB_VERSION_FILE_NAME), Charset.defaultCharset()));
        } catch (FileNotFoundException e) {
            writeCurrentVersion();
            version = DB_VERSION;
        } catch (IOException e) {
            throw new RuntimeException("Can't read database version file.");
        }

        if (version < DB_VERSION) {
            if (version == 1) {
                chatDao.executeRaw("alter table `chat` add column `customStateData` VARCHAR default null");
                version = 2;
            }
            if (version == 2) {
                chatDao.executeRaw("alter table `app` add column `translateLanguage` VARCHAR not null default uk");
                version = 3;
            }
        }
        writeCurrentVersion();
    }

    private void writeCurrentVersion() {
        String versionString = String.valueOf(DB_VERSION);
        try {
            Files.write(versionString, new File(DB_VERSION_FILE_NAME), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Can't update database version file.");
        }
    }

    @Override
    public Chat getOrCreateChat(long chatId, long userId) {
        Chat chat;
        try {
            chat = chatDao.queryBuilder()
                    .selectColumns()
                    .where()
                    .eq("chatId", chatId)
                    .and()
                    .eq("userId", userId)
                    .queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (chat == null) {
            chat = new Chat(chatId, userId);
        }
        return chat;
    }

    @Override
    public void saveChat(Chat chat) {
        chatDao.createOrUpdate(chat);
    }

    @Override
    public void deleteChat(Chat chat) {
        chatDao.delete(chat);
    }

    @Override
    public PendingApp getOrCreatePendingApp(long chatId, long userId) {
        PendingApp pendingApp;
        try {
            pendingApp = pendingAppsDao.queryBuilder()
                    .selectColumns()
                    .where()
                    .eq("chatId", chatId)
                    .and()
                    .eq("userId", userId)
                    .queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (pendingApp == null) {
            pendingApp = new PendingApp(chatId, userId);
        }
        return pendingApp;
    }

    @Override
    public PendingApp getPendingApp(long pendingAppId) {
        return pendingAppsDao.queryForId(pendingAppId);
    }

    @Override
    public void savePendingApp(PendingApp pendingApp) {
        pendingAppsDao.createOrUpdate(pendingApp);
    }

    @Override
    public void deletePendingApp(PendingApp pendingApp) {
        pendingAppsDao.delete(pendingApp);
    }

    @Override
    public App getApp(long id) {
        return appsDao.queryForId(id);
    }

    @Override
    public App createApp(PendingApp pendingApp, long targetChatId, long targetUserId) {
        App app = new App();

        app.packageName = pendingApp.packageName;
        app.keyFilePath = pendingApp.keyFilePath;
        app.chatId = targetChatId;
        app.userId = targetUserId;

        appsDao.create(app);
        return app;
    }

    @Override
    public void saveApp(App app) {
        appsDao.createOrUpdate(app);
    }

    @Override
    public void deleteApp(App app) {
        appsDao.delete(app);
    }

    @Override
    public Set<App> listAppsByChatId() {
        return appsDao.queryForAll().stream().collect(Collectors.toSet());
    }

    @Override
    public Set<App> listAppsByChatId(long chatId) {
        Set<App> apps;
        try {
            apps = appsDao.queryBuilder()
                    .selectColumns()
                    .where()
                    .eq("chatId", chatId)
                    .query()
                    .stream().collect(Collectors.toSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apps;
    }

    @Override
    public Set<App> listAppsByChatId(long chatId, long userId) {
        Set<App> apps;
        try {
            apps = appsDao.queryBuilder()
                    .selectColumns()
                    .where()
                    .eq("chatId", chatId)
                    .and()
                    .eq("userId", userId)
                    .query()
                    .stream().collect(Collectors.toSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apps;
    }

    @Override
    public Set<App> listAppsByUserId(long userId) {
        Set<App> apps;
        try {
            apps = appsDao.queryBuilder()
                    .selectColumns()
                    .where()
                    .eq("userId", userId)
                    .query()
                    .stream().collect(Collectors.toSet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return apps;
    }
}
