package application.models;

public enum Permission {
    USER("user_permissions"),
    MODERATOR("moderator_permissions"),
    ADMIN("admin_permissions");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
