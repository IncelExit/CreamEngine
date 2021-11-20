package org.incelexit.permissions;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import javax.annotation.Nullable;
import java.util.Set;

public class PermissionManager {
    private final Set<String> permittedRoles;

    public PermissionManager(Set<String> permittedRoles) {
        this.permittedRoles = permittedRoles;
    }

    public boolean checkPermissions(@Nullable Member member) {
        if(member == null)
            return false;

        for (Role role : member.getRoles()) {
            if (permittedRoles.contains(role.getName())) {
                return true;
            }
        }

        return false;
    }
}
