package org.incelexit.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public abstract class GuildMessageReceivedListener extends ThreadedListener {

    @Override
    public final void handleEvent(@NotNull GenericEvent event) {
        if (event instanceof GuildMessageReceivedEvent gmrEvent) {
            if(!gmrEvent.getAuthor().isBot() ) {
                handleGuildMessage(gmrEvent);
            }
        }
    }

    protected abstract void handleGuildMessage(GuildMessageReceivedEvent gmrEvent);
}
