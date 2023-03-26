package org.incelexit.creamengine.games.dodge.game.callbacks;

import net.dv8tion.jda.api.entities.Message;
import org.incelexit.creamengine.games.dodge.game.Dodgegame;
import org.incelexit.creamengine.util.MessageCallback;

public class RenderMessageSetterCallback implements MessageCallback {
    Dodgegame game;

    public RenderMessageSetterCallback(Dodgegame game) {
        this.game = game;
    }

    @Override
    public void operation(Message message) {
        this.game.setRenderMessageId(message.getId());
    }
}
