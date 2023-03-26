package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public abstract class GuildMessageReceivedListener extends ThreadedListener {

    @Override
    public final void handleEvent(@NotNull GenericEvent event) {
        if (event instanceof MessageReceivedEvent gmrEvent) {
            if(!gmrEvent.getAuthor().isBot() ) {
                handleGuildMessage(gmrEvent);
            }
        }
    }

    protected abstract void handleGuildMessage(MessageReceivedEvent gmrEvent);
}
