package org.incelexit.creamengine.bot;

import org.incelexit.creamengine.cache.CacheAllMembers;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class CEBot {

    JDA jda;

    public CEBot() {
        try {
            EnumSet<GatewayIntent> intents = EnumSet.of(
                    // We need messages in guilds to accept commands from users
                    GatewayIntent.GUILD_MESSAGES,
                    // We need voice states to connect to the voice channel
                    GatewayIntent.GUILD_VOICE_STATES,
                    GatewayIntent.GUILD_MEMBERS
            );

            JDABuilder jdaBuilder = JDABuilder.createLight(
                    "", intents);
            jdaBuilder.setActivity(Activity.playing("Juicing cows"));
            jdaBuilder.setMemberCachePolicy(new CacheAllMembers());
            this.jda = jdaBuilder.build();
        } catch (LoginException e) {
            e.printStackTrace();
            System.out.println("LOGIN FAILED");
        }
    }

    public void registerListener(EventListener... listeners) {
        this.jda.addEventListener((Object[]) listeners);
    }

    public void removeListener(EventListener... listeners) {
        this.jda.removeEventListener((Object) listeners);
    }
}
