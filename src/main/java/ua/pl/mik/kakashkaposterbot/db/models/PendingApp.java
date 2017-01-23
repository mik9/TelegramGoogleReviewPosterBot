package ua.pl.mik.kakashkaposterbot.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable
public class PendingApp {
    @DatabaseField(generatedId = true)
    public long pendingAppId;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    public long chatId;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    public long userId;

    @DatabaseField(canBeNull = true)
    public String packageName;
    @DatabaseField(canBeNull = true)
    public String keyFilePath;

    public PendingApp(long chatId, long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    public PendingApp() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingApp that = (PendingApp) o;
        return pendingAppId == that.pendingAppId &&
                chatId == that.chatId &&
                userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pendingAppId, chatId, userId);
    }
}
