package org.incelexit;

import org.incelexit.bot.CEBot;
import org.incelexit.listeners.*;

public class Main {
    public static void main(String[] args) {
        CEBot bot = new CEBot();

        bot.registerListener(new APIReadyListener());
        bot.registerListener(new DoPurgeListener());
        bot.registerListener(new ListPurgeListener());
        bot.registerListener(new MemberCountListener());
        bot.registerListener(new CommandLoggerListener());
        bot.registerListener(new WordGameListener(bot));
    }
}
