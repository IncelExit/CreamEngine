package org.incelexit.cache;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

public class CacheAllMembers implements MemberCachePolicy {

    @Override
    public boolean cacheMember(@NotNull Member member) {
        return true;
    }
}
