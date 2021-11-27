package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class CommandLoggerListener extends ThreadedListener {

    @Override
    protected void handleEvent(@NotNull GenericEvent event) {
        if(event instanceof GuildMessageReceivedEvent gmrEvent) {
            String message = gmrEvent.getMessage().getContentDisplay();
            if(message.startsWith("/")) {
                System.out.println(gmrEvent.getAuthor() + "    " + message);
            }
        }
    }
}
