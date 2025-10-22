package su.pank.transport.data.models;

public enum RouteType {
    URBAN("Городской"),
    MIXED("Смешанный"),
    SUBURBAN("Пригородный"),
    REGIONAL("Региональный"),
    UNKNOWN("Неизвестный");

    private final String displayName;

    RouteType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}