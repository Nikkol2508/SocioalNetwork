package application.models;

public enum NotificationSettingType {
    POST("POST"),
    POST_COMMENT("POST_COMMENT"),
    COMMENT_COMMENT("COMMENT_COMMENT"),
    FRIEND_REQUEST("FRIEND_REQUEST"),
    MESSAGE("MESSAGE");

    private final String value;

    NotificationSettingType(String value) {
        this.value = value;
    }
}
