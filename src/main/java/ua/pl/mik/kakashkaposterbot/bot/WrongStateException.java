package ua.pl.mik.kakashkaposterbot.bot;

import ua.pl.mik.kakashkaposterbot.db.models.ChatState;

public class WrongStateException extends BotAnswerException {
    public WrongStateException(ChatState state) {
        super("Can't do this now. " + state.description);
    }
}
