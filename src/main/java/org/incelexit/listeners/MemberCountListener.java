package org.incelexit.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MemberCountListener extends AdminCommandListener {

    private static final String COUNT_MEMBERS = "countMembers";

    public void handleAdminCommand(GuildMessageReceivedEvent gmrEvent) {
        String message = gmrEvent.getMessage().getContentDisplay().substring(1);

        Guild guild = gmrEvent.getGuild();
        TextChannel currentChannel = gmrEvent.getChannel();

        if(message.equals(COUNT_MEMBERS)) {
            countMembers(guild, currentChannel);
        }
    }

    public void countMembers(Guild guild, TextChannel channel) {
        channel.sendMessage(Integer.toString(guild.getMemberCount())).queue();
    }
}
