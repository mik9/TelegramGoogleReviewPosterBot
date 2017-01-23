package ua.pl.mik.kakashkaposterbot.db.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Chat {
    @DatabaseField(id = true, uniqueCombo = true)
    public long chatId;
    @DatabaseField(canBeNull = false, uniqueCombo = true)
    public long userId;
    @DatabaseField(canBeNull = false)
    public ChatState state = ChatState.NO_STATE;
    @DatabaseField(canBeNull = true)
    public String customStateData;

    public Chat() {
    }

    public Chat(long chatId, long userId) {
        this.chatId = chatId;
        this.userId = userId;
    }

    public void clearState() {
        state = ChatState.NO_STATE;
        customStateData = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Chat chat = (Chat) o;

        if (chatId != chat.chatId) return false;
        return userId == chat.userId;
    }

    @Override
    public int hashCode() {
        int result = (int) (chatId ^ (chatId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}
