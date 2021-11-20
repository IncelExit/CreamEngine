package org.incelexit.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.jetbrains.annotations.NotNull;

public class APIReadyListener extends ThreadedListener {

    public void handleEvent(@NotNull GenericEvent event) {
        if(event instanceof ReadyEvent) {
            System.out.println("API is ready.");
        }
    }
}
