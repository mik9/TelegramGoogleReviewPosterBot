package ua.pl.mik.kakashkaposterbot.db.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.time.LocalDateTime;
import java.util.Objects;

@DatabaseTable
public class App {
    @DatabaseField(generatedId = true)
    public long id;
    @DatabaseField(canBeNull = false)
    public long chatId;
    @DatabaseField(canBeNull = false)
    public long userId;

    @DatabaseField(canBeNull = false)
    public String packageName;
    @DatabaseField(canBeNull = true)
    public String name;
    @DatabaseField(canBeNull = false)
    public String keyFilePath;
    @DatabaseField
    public boolean enabled = true;
    @DatabaseField(canBeNull = true, dataType = DataType.SERIALIZABLE)
    public LocalDateTime lastCheckTime;
    @DatabaseField(canBeNull = true)
    public String lastReviewId;
    @DatabaseField(canBeNull = false)
    public String translateLanguage = "uk";

    public String getName() {
        return name != null ? name : packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        App app = (App) o;
        return id == app.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
