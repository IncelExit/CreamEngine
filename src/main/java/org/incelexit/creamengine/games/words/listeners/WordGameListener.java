package org.incelexit.creamengine.games.words.listeners;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.incelexit.creamengine.games.words.WordListManager;
import org.incelexit.creamengine.games.words.Wordgame;
import org.incelexit.creamengine.listeners.AdminCommandListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordGameListener extends AdminCommandListener {

    List<String> allowedChannels = List.of("wordgame", "word-game", "bot-testing", "bot-log");

    Set<String> channelsWithRunningGames = new HashSet<>();

    @Override
    public void handleAdminCommand(@NotNull MessageReceivedEvent gmrEvent) {
        if (allowedChannels.contains(gmrEvent.getChannel().getName())) {
            String message = gmrEvent.getMessage().getContentRaw();
            MessageChannel channel = gmrEvent.getChannel();
            if ("/wordgame".equals(message)) {
                if (!channelsWithRunningGames.contains(channel.getId())) {
                    Wordgame game = new Wordgame(gmrEvent.getChannel());
                    game.start();
                }
            } else if ("/finish".equals(message)) {
                channelsWithRunningGames.remove(channel.getId());
            } else if (message.startsWith("/addword")) {
                WordListManager.addWord(message, channel);
            } else if (message.startsWith("/removeword")) {
                WordListManager.removeWord(message, channel);
            }
        }
    }
}
