package org.incelexit.creamengine.games.words;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.incelexit.creamengine.games.words.common.FileHandler;
import org.incelexit.creamengine.util.ChannelMessenger;

public class WordListManager {
    public static void addWord(String message, MessageChannel channel) {
        String[] split = message.split(" ");
        boolean wordAdded = false;
        if (split.length > 1) {
            wordAdded = FileHandler.addWord(split[1]);
        }
        if (wordAdded) {
            reply("Word added.", channel);
        } else {
            reply("Could not add word, it was already in the word list.", channel);
        }
    }

    public static void removeWord(String message, MessageChannel channel) {
        String[] split = message.split(" ");
        boolean wordRemoved = false;
        if (split.length > 1) {
            wordRemoved = FileHandler.removeWord(split[1]);
        }
        if (wordRemoved) {
            reply("Word removed.", channel);
        } else {
            reply("Could not remove word, it was not in the word list.", channel);
        }
    }

    private static void reply(String message, MessageChannel channel) {
        ChannelMessenger channelMessenger = new ChannelMessenger(channel);
        channelMessenger.sendMessage(message);
    }
}
