package org.incelexit.creamengine.games.words.callbacks;

import net.dv8tion.jda.api.entities.Message;
import org.incelexit.creamengine.games.words.Wordgame;
import org.incelexit.creamengine.util.MessageCallback;

public class LetterMessageSetterCallback implements MessageCallback {
    Wordgame game;

    public LetterMessageSetterCallback(Wordgame game) {
        this.game = game;
    }

    @Override
    public void operation(Message message) {
        this.game.setLetterMessageId(message.getId());
    }
}
