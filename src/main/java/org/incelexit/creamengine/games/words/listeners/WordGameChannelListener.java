package org.incelexit.creamengine.games.words.listeners;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.incelexit.creamengine.games.words.Wordgame;
import org.incelexit.creamengine.listeners.GuildMessageReceivedListener;

public class WordGameChannelListener extends GuildMessageReceivedListener {

    private final Wordgame game;
    private final MessageChannel channel;


    public WordGameChannelListener(Wordgame game, MessageChannel channel) {
        this.game = game;
        this.channel = channel;
    }

    @Override
    protected void handleGuildMessage(MessageReceivedEvent gmrEvent) {
        if (this.channel.getId().equals(gmrEvent.getChannel().getId())) {
            Message message = gmrEvent.getMessage();
            String messageText = gmrEvent.getMessage().getContentDisplay();
            switch (messageText) {
                case "/words" -> game.printFoundWords();
                case "/rules" -> game.printGameRules();
                case "/letters" -> game.printLetters();
                case "/score" -> game.printPoints();
                case "/finish" -> game.finish();
                default -> {
                    if (messageText.matches("[a-zA-Z]*")) game.processNextWord(gmrEvent.getAuthor(), message);
                }
            }
        }
    }
}
