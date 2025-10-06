package su.pank.transport.model;

import javafx.beans.property.*;

public class Route {
    private final IntegerProperty id;
    private final IntegerProperty routeNumber;
    private final ObjectProperty<RoutePoint> startPoint;
    private final ObjectProperty<RoutePoint> endPoint;
    private final StringProperty specialCategory;
    private final StringProperty routeType;

    public Route(int id, int routeNumber, RoutePoint startPoint, RoutePoint endPoint, String specialCategory) {
        this.id = new SimpleIntegerProperty(id);
        this.routeNumber = new SimpleIntegerProperty(routeNumber);
        this.startPoint = new SimpleObjectProperty<>(startPoint);
        this.endPoint = new SimpleObjectProperty<>(endPoint);
        this.specialCategory = new SimpleStringProperty(specialCategory != null ? specialCategory : "");
        this.routeType = new SimpleStringProperty(determineRouteType(routeNumber));
    }

    private String determineRouteType(int number) {
        if (number >= 1 && number <= 199) return "Городской";
        else if (number >= 200 && number <= 299) return "Смешанный";
        else if (number >= 300 && number <= 399) return "Пригородный";
        else if (number >= 400 && number <= 999) return "Региональный";
        return "Неизвестный";
    }

    public int getId() { return id.get(); }
    public int getRouteNumber() { return routeNumber.get(); }
    public void setRouteNumber(int value) {
        routeNumber.set(value);
        routeType.set(determineRouteType(value));
    }
    public IntegerProperty routeNumberProperty() { return routeNumber; }

    public RoutePoint getStartPoint() { return startPoint.get(); }
    public void setStartPoint(RoutePoint value) { startPoint.set(value); }
    public ObjectProperty<RoutePoint> startPointProperty() { return startPoint; }

    public RoutePoint getEndPoint() { return endPoint.get(); }
    public void setEndPoint(RoutePoint value) { endPoint.set(value); }
    public ObjectProperty<RoutePoint> endPointProperty() { return endPoint; }

    public String getSpecialCategory() { return specialCategory.get(); }
    public void setSpecialCategory(String value) { specialCategory.set(value); }
    public StringProperty specialCategoryProperty() { return specialCategory; }

    public String getRouteType() { return routeType.get(); }
    public StringProperty routeTypeProperty() { return routeType; }

    public String getBadgeColor() {
        int num = getRouteNumber();
        if (num >= 1 && num <= 199) return "#B8F1B9"; // City - Green
        else if (num >= 200 && num <= 299) return "#DFE0FF"; // Mixed - Purple
        else if (num >= 300 && num <= 399) return "#F9E287"; // Suburban - Yellow
        return "#E3E9EA"; // Regional - Grey
    }

    public String getTextColor() {
        int num = getRouteNumber();
        if (num >= 1 && num <= 199) return "#1E5127";
        else if (num >= 200 && num <= 299) return "#3B4279";
        else if (num >= 300 && num <= 399) return "#534600";
        return "#161D1D";
    }
}