package ua.pl.mik.kakashkaposterbot.db.models;

public enum ChatState {
    NO_STATE("?"),
    WAITONG_FOR_PACKAGE_NAME("Я очікую package name. Хочете скасувати (/abort)?"),
    WAITING_FOR_KEY_FILE("Я очікую ключ від сервіс аккаунту. Хочете скасувати створення задачі (/abort)?"),
    WAITING_FOR_APP_TO_STOP("Я очіку назву задачі, що потрібно вимкнути. Хочете скасувати (/abort)?"),
    WAITING_FOR_APP_TO_START("Я очіку назву задачі, що потрібно увімкнути. Хочете скасувати (/abort)?"),
    WAITING_FOR_APP_TO_CHANGE_CHAT("Я очіку назву задачі, для якої потрібно змінити чат. Хочете скасувати (/abort)?"),
    WAITING_FOR_APP_NAME("Я очікую нову назву для задачі. Хочете скасувати (/abort)?"),
    WAITING_FOR_LANGUAGE("Я очікую мову для задачі. Хочете скасувати (/abort)?");

    public String description;

    ChatState(String description) {
        this.description = description;
    }
}
