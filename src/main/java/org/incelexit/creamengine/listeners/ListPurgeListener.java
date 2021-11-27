package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.incelexit.creamengine.util.ChannelMessenger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ListPurgeListener extends PurgeListener {

    private static final String LIST_PURGE = "listPurge";

    @Override
    public void handleAdminCommand(GuildMessageReceivedEvent gmrEvent) {
        String message = gmrEvent.getMessage().getContentDisplay().substring(1);

        Guild guild = gmrEvent.getGuild();
        TextChannel currentChannel = gmrEvent.getChannel();

        if (message.equals(LIST_PURGE)) {
            listPurge(guild, currentChannel);
        }
    }

    public void listPurge(Guild guild, TextChannel currentChannel) {
        //asynchronous loading of members is necessary
        Task<List<Member>> memberLoadingTask = guild.loadMembers();

        List<Member> members = memberLoadingTask.get();
        listPurge(members, guild, currentChannel);
    }

    protected void listPurge(List<Member> members, Guild guild, TextChannel currentChannel) {

        Set<User> usersSeenRecently = new HashSet<>();
        for (TextChannel channel : guild.getTextChannels()) {
            usersSeenRecently.addAll(getUsersSeenInChannel(channel, Duration.of(MAXIMUM_DAYS_SINCE_LAST_MESSAGE, ChronoUnit.DAYS)));
        }

        Set<User> inactiveUsers = members.stream().map(Member::getUser).collect(Collectors.toSet());

        inactiveUsers.removeAll(usersSeenRecently);

        inactiveUsers = filterByRoles(inactiveUsers, guild);

        listInactiveUsers(currentChannel, inactiveUsers);
    }

    private void listInactiveUsers(TextChannel currentChannel, Set<User> inactiveUsers) {
        ChannelMessenger channelMessenger = new ChannelMessenger(currentChannel);
        List<String> messages = new ArrayList<>();
        messages.add("Following users have not posted for " + MAXIMUM_DAYS_SINCE_LAST_MESSAGE + " days:");
        for (User inactiveUser : inactiveUsers) {
            messages.add(inactiveUser.getAsMention());
        }
        channelMessenger.sendLinesInMinimumMessages(messages);
    }
}
