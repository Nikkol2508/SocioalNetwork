package application.models;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

public enum Role {
    USER(Set.of(Permission.CRUD_POST)),
    MODERATOR(Set.of(Permission.CRUD_POST, Permission.POST_AND_USER_BLOCKING)),
    ADMIN(Set.of(Permission.CRUD_POST, Permission.POST_AND_USER_BLOCKING, Permission.MANAGE_ADMINS_AND_MODERATORS));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }
}
