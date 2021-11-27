package org.incelexit.creamengine.games.words.listeners;

import org.incelexit.creamengine.games.words.game.Game;
import org.incelexit.creamengine.listeners.GuildMessageReceivedListener;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WordGameChannelListener extends GuildMessageReceivedListener {

    private final Game game;
    private final TextChannel channel;


    public WordGameChannelListener(Game game, TextChannel channel) {
        this.game = game;
        this.channel = channel;
    }

    @Override
    protected void handleGuildMessage(GuildMessageReceivedEvent gmrEvent) {
        if(this.channel.getName().equals(gmrEvent.getChannel().getName())) {
            String message = gmrEvent.getMessage().getContentDisplay();
            switch (message) {
                case "/finish" -> game.finish();
                case "/words" -> game.showAlreadyUsedWords();
                case "/rules" -> game.printGameRules();
                case "/letters" -> game.printLetters();
                default -> game.processNextWord(gmrEvent.getAuthor(), message);
            }
        }
    }
}
