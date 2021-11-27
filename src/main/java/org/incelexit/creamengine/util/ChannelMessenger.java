package org.incelexit.creamengine.util;

import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class ChannelMessenger {

    // length of \n
    private static final int LINEBREAK_LENGTH = 2;

    private final TextChannel channel;

    public TextChannel getChannel() {
        return this.channel;
    }

    public ChannelMessenger(TextChannel channel) {
        this.channel = channel;
    }

    public void sendLinesInMinimumMessages(List<String> lines) {
        lines = breakUpLines(lines);

        StringBuilder messageBuilder = new StringBuilder();

        for (String line : lines) {
            if(line.length() > 0) {
                if (messageBuilder.length() + line.length() + LINEBREAK_LENGTH < 2000) {
                    messageBuilder.append(line).append("\n");
                } else {
                    channel.sendMessage(messageBuilder.toString()).queue();
                    messageBuilder = new StringBuilder(line);
                }
            }
        }
        if (messageBuilder.length() > 0)
            channel.sendMessage(messageBuilder).queue();
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

    public void sendMessage(String message) {
        sendLinesInMinimumMessages(breakUpLines(List.of(message)));
    }
}
