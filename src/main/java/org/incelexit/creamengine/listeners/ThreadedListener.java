package org.incelexit.creamengine.listeners;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

public abstract class ThreadedListener implements EventListener {

    @Override
    public final void onEvent(@NotNull GenericEvent event) {
        Thread thread = new Thread(() -> handleEvent(event));
        thread.setUncaughtExceptionHandler(new ListenerThreadExceptionHandler());
        thread.start();
    }

    protected abstract void handleEvent(@NotNull GenericEvent event);

    private static class ListenerThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread th, Throwable ex) {
            System.out.println("Uncaught exception: " + ex);
        }
    }
}
