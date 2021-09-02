package application.models;

public enum NotificationType {
    POST(1),
    POST_COMMENT(2),
    COMMENT_COMMENT(3),
    FRIEND_REQUEST(4),
    MESSAGE(5);

    private final int typeId;

    NotificationType(int typeId) {
        this.typeId = typeId;
    }
}
