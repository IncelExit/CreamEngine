package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PurgeListener extends AdminCommandListener {
    protected static final Set<String> PURGE_REMOVE_ROLES = Set.of("Incel", "Friends", "Ex-Incel", "Helper");
    protected static final String PURGE_ASSIGN_ROLE = "Friendly Lurker";

    protected static final long MAXIMUM_DAYS_SINCE_LAST_MESSAGE = 42;

    protected Set<User> getUsersSeenInChannel(TextChannel channel, Duration maxTimeSinceLastMessage) {
        MessageHistory history = channel.getHistory();
        OffsetDateTime earliestSearchDate = OffsetDateTime.now().minus(maxTimeSinceLastMessage);
        OffsetDateTime earliestMessageTime = OffsetDateTime.now();
        Set<User> usersSeenRecently = new HashSet<>();
        System.out.println("Loading channel " + channel.getName());
        int loadedMessages = 0;

        while (earliestMessageTime.isAfter(earliestSearchDate) && loadedMessages % 100 == 0) {
            List<Message> messages = history.retrievePast(100).complete();
            loadedMessages += messages.size();
            System.out.println(loadedMessages);
            for (Message message : messages) {
                OffsetDateTime messageTime = message.getTimeCreated();
                if (messageTime.isAfter(earliestSearchDate)) {
                    usersSeenRecently.add(message.getAuthor());
                }
                if (messageTime.isBefore(earliestMessageTime)) {
                    earliestMessageTime = messageTime;
                }
            }
        }
        return usersSeenRecently;
    }

    @NotNull
    protected Set<User> filterByRoles(Set<User> inactiveUsers, Guild guild) {
        inactiveUsers = inactiveUsers.stream().filter(user -> {
            Member member = guild.getMember(user);
            return member != null && hasPurgedRole(member);
        }).collect(Collectors.toSet());
        return inactiveUsers;
    }


    protected boolean hasPurgedRole(Member member) {
        for (Role role : member.getRoles()) {
            if (PURGE_REMOVE_ROLES.contains(role.getName())) {
                return true;
            }
        }
        return false;
    }
}
