package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.incelexit.creamengine.util.ChannelMessenger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DoPurgeListener extends PurgeListener {

    private static final String DO_PURGE = "doPurge";

    @Override
    public void handleAdminCommand(GuildMessageReceivedEvent gmrEvent) {
        String message = gmrEvent.getMessage().getContentDisplay().substring(1);

        Guild guild = gmrEvent.getGuild();
        TextChannel currentChannel = gmrEvent.getChannel();

        if (message.equals(DO_PURGE)) {
            doPurge(guild, currentChannel);
        }
    }

    public void doPurge(Guild guild, TextChannel currentChannel) {
        //asynchronous loading of members is necessary
        Task<List<Member>> memberLoadingTask = guild.loadMembers();
        memberLoadingTask.onSuccess(members -> doPurge(members, guild, currentChannel));
    }

    private void doPurge(List<Member> members, Guild guild, TextChannel currentChannel) {
        Set<User> inactiveUsers = members.stream().map(Member::getUser).collect(Collectors.toSet());

        inactiveUsers.removeAll(getRecentlySeenUsers(guild));

        inactiveUsers = filterByRoles(inactiveUsers, guild);

        reassignRolesToMembers(guild, currentChannel, inactiveUsers);
    }

    private void reassignRolesToMembers(Guild guild, TextChannel currentChannel, Set<User> inactiveUsers) {

        Set<Role> rolesToRemove = new HashSet<>();
        Set<Role> rolesToAdd = new HashSet<>();

        getRolesToAddAndRemove(guild, rolesToRemove, rolesToAdd);

        reassignRolesToMember(guild, inactiveUsers, rolesToRemove, rolesToAdd);

        listReassignedUsers(currentChannel, inactiveUsers);
    }

    private void reassignRolesToMember(Guild guild, Set<User> inactiveUsers, Set<Role> rolesToRemove, Set<Role> rolesToAdd) {
        for (User user : inactiveUsers) {
            Member member = guild.getMember(user);

            if (member == null) {
                continue;
            }

            for (Role roleToRemove : rolesToRemove) {
                guild.removeRoleFromMember(member, roleToRemove).queue();
            }

            for (Role roleToAdd : rolesToAdd) {
                guild.addRoleToMember(member, roleToAdd).queue();
            }
        }
    }

    private void getRolesToAddAndRemove(Guild guild, Set<Role> rolesToRemove, Set<Role> rolesToAdd) {
        List<Role> roles = guild.getRoles();

        for (Role role : roles) {
            String roleName = role.getName();
            if (PURGE_REMOVE_ROLES.contains(roleName)) {
                rolesToRemove.add(role);
            }
            if (PURGE_ASSIGN_ROLE.equals(roleName)) {
                rolesToAdd.add(role);
            }
        }
    }

    private void listReassignedUsers(TextChannel currentChannel, Set<User> inactiveUsers) {
        ChannelMessenger channelMessenger = new ChannelMessenger(currentChannel);
        List<String> messages = new ArrayList<>();
        messages.add("Following users have not posted for " + MAXIMUM_DAYS_SINCE_LAST_MESSAGE + " days and were assigned the \"Friendly lurker\" role:");
        for (User inactiveUser : inactiveUsers) {
            messages.add(inactiveUser.getAsMention());
        }
        channelMessenger.sendLinesInMinimumMessages(messages);
    }

    @NotNull
    private Set<User> getRecentlySeenUsers(Guild guild) {
        Set<User> usersSeenRecently = new HashSet<>();
        for (TextChannel channel : guild.getTextChannels()) {
            usersSeenRecently.addAll(getUsersSeenInChannel(channel, Duration.of(MAXIMUM_DAYS_SINCE_LAST_MESSAGE, ChronoUnit.DAYS)));
        }
        return usersSeenRecently;
    }
}
