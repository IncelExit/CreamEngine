package org.incelexit.creamengine.util;

import net.dv8tion.jda.api.entities.Message;

public class MessagePinCallback implements MessageCallback {
    @Override
    public void operation(Message message) {
        message.getChannel().pinMessageById(message.getId()).queue();
    }
}
