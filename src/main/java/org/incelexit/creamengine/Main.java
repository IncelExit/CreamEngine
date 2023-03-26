package org.incelexit.creamengine;

import org.incelexit.creamengine.bot.CEBot;
import org.incelexit.creamengine.listeners.DeleteUserMessagesListener;

public class Main {
    public static void main(String[] args) {
        CEBot bot = CEBot.getBot();

        /*bot.registerListener(new APIReadyListener());
        bot.registerListener(new DodgeGameListener());
        bot.registerListener(new DoPurgeListener());
        bot.registerListener(new ListPurgeListener());
        bot.registerListener(new MemberCountListener());
        bot.registerListener(new CommandLoggerListener());
        bot.registerListener(new WordGameListener());*/
        bot.registerListener(new DeleteUserMessagesListener());

    }
}
