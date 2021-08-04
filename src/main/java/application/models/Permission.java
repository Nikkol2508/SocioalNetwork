package application.models;

public enum Permission {
    CRUD_POST("posts:crud"),
    POST_AND_USER_BLOCKING("user:blocking, post:blocking"),
    MANAGE_ADMINS_AND_MODERATORS("admins:manage, moderators:manage");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
