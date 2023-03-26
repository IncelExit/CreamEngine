package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class CommandLoggerListener extends ThreadedListener {

    @Override
    protected void handleEvent(@NotNull GenericEvent event) {
        if(event instanceof MessageReceivedEvent gmrEvent) {
            String message = gmrEvent.getMessage().getContentDisplay();
            if(message.startsWith("/")) {
                System.out.println(gmrEvent.getAuthor() + "    " + message);
            }
        }
    }
}
