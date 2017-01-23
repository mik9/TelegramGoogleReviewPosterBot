package ua.pl.mik.kakashkaposterbot.bot;

import ua.pl.mik.kakashkaposterbot.db.models.ChatState;

public class WrongStateException extends BotAnswerException {
    public WrongStateException(ChatState state) {
        super("Не можу це виконати. " + state.description);
    }
}
