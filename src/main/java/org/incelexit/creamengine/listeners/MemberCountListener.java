package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MemberCountListener extends AdminCommandListener {

    private static final String COUNT_MEMBERS = "countMembers";

    public void handleAdminCommand(MessageReceivedEvent gmrEvent) {
        String message = gmrEvent.getMessage().getContentDisplay().substring(1);

        Guild guild = gmrEvent.getGuild();
        MessageChannel currentChannel = gmrEvent.getChannel();

        if(message.equals(COUNT_MEMBERS)) {
            countMembers(guild, currentChannel);
        }
    }

    public void countMembers(Guild guild, MessageChannel channel) {
        channel.sendMessage(Integer.toString(guild.getMemberCount())).queue();
    }
}
