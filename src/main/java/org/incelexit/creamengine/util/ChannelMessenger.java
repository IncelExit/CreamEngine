package org.incelexit.creamengine.util;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChannelMessenger {

    // length of \n
    private static final int LINEBREAK_LENGTH = 2;

    private final MessageChannel channel;

    public MessageChannel getChannel() {
        return this.channel;
    }

    public ChannelMessenger(MessageChannel channel) {
        this.channel = channel;
    }

    public void unpinMessage(@Nullable String messageId) {
        if (messageId != null) {
            channel.unpinMessageById(messageId).queue();
        }
    }

    public void unpinAllMessages() {
        channel.retrievePinnedMessages().complete().forEach(
                m -> channel.unpinMessageById(m.getId()).complete());
    }

    public void sendLinesInMinimumMessages(List<String> lines, MessageCallback... callbacks) {

        lines = breakUpLines(lines);

        StringBuilder messageBuilder = new StringBuilder();

        for (String line : lines) {
            if (line.length() > 0) {
                if (messageBuilder.length() + line.length() + LINEBREAK_LENGTH < 2000) {
                    messageBuilder.append(line).append("\n");
                } else {
                    channel.sendMessage(messageBuilder.toString()).queue();
                    messageBuilder = new StringBuilder(line);
                }
            }
        }
        if (messageBuilder.length() > 0)
            channel.sendMessage(messageBuilder).queue(
                    message -> {
                        for (MessageCallback callback : callbacks)
                            callback.operation(message);
                    });
    }

    private List<String> breakUpLines(List<String> lines) {
        List<String> newLines = new ArrayList<>();
        String lineChunk;
        int maxLengthWithoutLinebreak = 2000 - LINEBREAK_LENGTH;
        for (String line : lines) {
            while (line.length() > maxLengthWithoutLinebreak) {
                lineChunk = line.substring(0, maxLengthWithoutLinebreak);
                newLines.add(lineChunk);
                line = line.substring(maxLengthWithoutLinebreak);
            }
            newLines.add(line);
        }
        return newLines;
    }

    public void sendMessage(String message, MessageCallback... messageCallbacks) {
        sendLinesInMinimumMessages(breakUpLines(List.of(message)), messageCallbacks);
    }

    public void sendMessage(String message) {
        sendMessage(message, new MessageCallback[0]);
    }

    public void editMessage(String messageId, String newContent) {
        channel.editMessageById(messageId, newContent).queue();
    }

    public void deleteMessages(List<Message> messages) {
            channel.purgeMessages(messages);
    }

    public void deleteMessage(Message message) {
        deleteMessage(message.getId());
    }

    public void deleteMessage(String messageId) {
        channel.deleteMessageById(messageId).queue();
    }
}
