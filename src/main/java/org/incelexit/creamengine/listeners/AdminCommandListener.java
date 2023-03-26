package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.incelexit.creamengine.permissions.PermissionManager;

import java.util.Set;

public abstract class AdminCommandListener extends SlashCommandListener {

    private static final PermissionManager permissionManager = new PermissionManager(Set.of("mod"));


    @Override
    protected void handleSlashCommand(MessageReceivedEvent gmrEvent) {
        if(permissionManager.checkPermissions(gmrEvent.getMember())) {
            handleAdminCommand(gmrEvent);
        }
    }

    protected  abstract void handleAdminCommand(MessageReceivedEvent gmrEvent);
}
