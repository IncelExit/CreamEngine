package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class SlashCommandListener extends GuildMessageReceivedListener {

    public final void handleGuildMessage(MessageReceivedEvent gmrEvent) {
        if (gmrEvent.getMessage().getContentDisplay().startsWith("/")) {
            handleSlashCommand(gmrEvent);
        }
    }

    protected abstract void handleSlashCommand(MessageReceivedEvent gmrEvent);
}
