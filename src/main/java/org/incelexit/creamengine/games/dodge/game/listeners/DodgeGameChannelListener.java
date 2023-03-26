package org.incelexit.creamengine.games.dodge.game.listeners;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.incelexit.creamengine.games.dodge.game.Dodgegame;
import org.incelexit.creamengine.listeners.GuildMessageReceivedListener;

public class DodgeGameChannelListener extends GuildMessageReceivedListener {

    private final Dodgegame game;
    private final MessageChannel channel;


    public DodgeGameChannelListener(Dodgegame game, MessageChannel channel) {
        this.game = game;
        this.channel = channel;
    }

    @Override
    protected void handleGuildMessage(MessageReceivedEvent gmrEvent) {
        if (this.channel.getId().equals(gmrEvent.getChannel().getId())) {
            String message = gmrEvent.getMessage().getContentDisplay();
            if(message.matches("[0-9]?[wasd]")) {
                System.out.println("hi");
                game.processInput(gmrEvent.getMessage());
            } else if ("/finish".equals(message)) {
                game.finish();
            }
        }
    }
}
