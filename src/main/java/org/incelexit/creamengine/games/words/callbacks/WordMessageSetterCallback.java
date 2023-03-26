package org.incelexit.creamengine.games.words.callbacks;

import net.dv8tion.jda.api.entities.Message;
import org.incelexit.creamengine.games.words.Wordgame;
import org.incelexit.creamengine.util.MessageCallback;

public class WordMessageSetterCallback implements MessageCallback {
    Wordgame game;

    public WordMessageSetterCallback(Wordgame game) {
        this.game = game;
    }

    @Override
    public void operation(Message message) {
        this.game.setWordMessageId(message.getId());
    }
}
