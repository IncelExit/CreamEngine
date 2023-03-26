package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.incelexit.creamengine.util.ChannelMessenger;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteUserMessagesListener extends AdminCommandListener {

    private static final String DELETE_USER_MESSAGES = "deleteUserMessages";
    private ChannelMessenger channelMessenger;

    public void handleAdminCommand(MessageReceivedEvent gmrEvent) {
        String message = gmrEvent.getMessage().getContentDisplay().substring(1);

        Guild guild = gmrEvent.getGuild();
        MessageChannel currentChannel = gmrEvent.getChannel();
        this.channelMessenger = new ChannelMessenger(currentChannel);

        if (message.startsWith(DELETE_USER_MESSAGES)) {
            String[] split = message.split(" ");
            if (split.length > 1) {
                String username = split[1];
                deleteMessagesForUser(guild, currentChannel, username);
            }
        }
    }

    public void deleteMessagesForUser(Guild guild, MessageChannel currentChannel, String username) {
        List<Member> matchingMembers = guild.getMembersByName(username, false);
        /*
        if (matchingMembers.size() == 0) {
            channelMessenger.sendMessage("No matching member found.");
            return;
        }
        if (matchingMembers.size() > 1) {
            channelMessenger.sendMessage("More than one matching member found:\n" +
                                         matchingMembers.stream()
                                                 .map(m -> m.getUser().getName())
                                                 .reduce("", (n1, n2) -> (n1 + "\n" + n2)));
            return;
        }

        User user = matchingMembers.get(0).getUser();
         */

        MessageHistory history = MessageHistory.getHistoryFromBeginning(currentChannel).complete();
        List<Message> messages = history.getRetrievedHistory();
        List<Message> messagesToDelete;
        while (messages.size() > 0) {
            messagesToDelete = messages.stream().filter(m -> m.getAuthor().getName().equals(username)).collect(Collectors.toList());
            channelMessenger.deleteMessages(messagesToDelete);
            messages = history.retrieveFuture(100).complete();
            System.out.println("Processing messages between " + messages.get(0).getTimeCreated() + " and "
                               + messages.get(messages.size() - 1).getTimeCreated() + ".");
            System.out.println("Deleted " + messagesToDelete.size() + " messages.");
        }
    }
}
