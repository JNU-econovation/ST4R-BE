package star.common.constants;

import lombok.Getter;

@Getter
public enum SortField {
    CREATED_AT("createdAt", "createdAt"),
    VIEW_COUNT("viewCount", "viewCount"),
    HEART_COUNT("likeCount", "heartCount");

    private final String requestField;
    private final String dbField;

    SortField(String requestField, String dbField) {
        this.requestField = requestField;
        this.dbField = dbField;
    }

    public static SortField fromRequestKey(String requestKey) {
        for (SortField field : values()) {
            if (field.getRequestField().equals(requestKey)) {
                return field;
            }
        }
        return null;
    }
}