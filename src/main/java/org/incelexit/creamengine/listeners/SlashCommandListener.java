package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class SlashCommandListener extends GuildMessageReceivedListener {

    public final void handleGuildMessage(GuildMessageReceivedEvent gmrEvent) {
        if (gmrEvent.getMessage().getContentDisplay().startsWith("/")) {
            handleSlashCommand(gmrEvent);
        }
    }

    protected abstract void handleSlashCommand(GuildMessageReceivedEvent gmrEvent);
}
