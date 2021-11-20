package org.incelexit.listeners;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.incelexit.permissions.PermissionManager;

import java.util.Set;

public abstract class AdminCommandListener extends SlashCommandListener {

    private static final PermissionManager permissionManager = new PermissionManager(Set.of("mod"));


    @Override
    protected void handleSlashCommand(GuildMessageReceivedEvent gmrEvent) {
        if(permissionManager.checkPermissions(gmrEvent.getMember())) {
            handleAdminCommand(gmrEvent);
        }
    }

    protected  abstract void handleAdminCommand(GuildMessageReceivedEvent gmrEvent);
}
