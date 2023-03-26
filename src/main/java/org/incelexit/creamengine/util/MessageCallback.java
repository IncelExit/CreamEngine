package org.incelexit.creamengine.util;

import net.dv8tion.jda.api.entities.Message;

public interface MessageCallback {
    void operation(Message message);
}
