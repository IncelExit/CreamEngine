package org.incelexit.creamengine.games.dodge.game.listeners;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.incelexit.creamengine.games.dodge.game.Dodgegame;
import org.incelexit.creamengine.listeners.AdminCommandListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DodgeGameListener extends AdminCommandListener {

    List<String> allowedChannels = List.of("dodgegame");

    Set<String> channelsWithRunningGames = new HashSet<>();

    @Override
    public void handleAdminCommand(@NotNull MessageReceivedEvent gmrEvent) {
        if (allowedChannels.contains(gmrEvent.getChannel().getName())) {
            String message = gmrEvent.getMessage().getContentRaw();
            MessageChannel channel = gmrEvent.getChannel();
            switch (message) {
                case "/dodgegamestart" -> {
                    if (!channelsWithRunningGames.contains(channel.getId())) {
                        Dodgegame game = new Dodgegame(gmrEvent.getChannel());
                        game.start();
                    }
                }
                case "/dodgegamefinish" -> channelsWithRunningGames.remove(channel.getId());
            }
        }
    }
}
