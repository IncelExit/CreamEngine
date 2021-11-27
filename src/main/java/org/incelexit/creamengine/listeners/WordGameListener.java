package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.incelexit.creamengine.bot.CEBot;
import org.incelexit.creamengine.games.words.game.Game;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordGameListener extends AdminCommandListener {

    private static final Map<TextChannel, Game> games = new HashMap<>();
    private final CEBot bot;

    public WordGameListener(CEBot bot) {
        this.bot = bot;
    }

    List<String> allowedChannels = List.of("wordgame", "word-game", "bot-testing", "bot-log");


    @Override
    public void handleAdminCommand(@NotNull GuildMessageReceivedEvent gmrEvent) {
        if (allowedChannels.contains(gmrEvent.getChannel().getName())) {
            if (gmrEvent.getMessage().getContentDisplay().startsWith("/wordgame")) {
                startGame(gmrEvent.getChannel());
            }
        }
    }

    private void startGame(TextChannel channel) {
        Game game = games.get(channel);
        if (game != null) {
            game.finish();
        }
        game = new Game(channel, bot);
        game.start();
        games.put(channel, game);
    }
}
