package su.pank.transport.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;

public class Route {
    private final IntegerProperty id;
    private final IntegerProperty routeNumber;
    private final ObjectProperty<RoutePoint> startPoint;
    private final ObjectProperty<RoutePoint> endPoint;
    private final ObservableList<String> specialCategories;
    private final StringProperty routeType;

    public Route(int id, int routeNumber, RoutePoint startPoint, RoutePoint endPoint, String specialCategory) {
        this.id = new SimpleIntegerProperty(id);
        this.routeNumber = new SimpleIntegerProperty(routeNumber);
        this.startPoint = new SimpleObjectProperty<>(startPoint);
        this.endPoint = new SimpleObjectProperty<>(endPoint);
        this.specialCategories = FXCollections.observableArrayList();
        if (specialCategory != null && !specialCategory.isEmpty()) {
            this.specialCategories.addAll(Arrays.asList(specialCategory.split(",")));
        }
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

    public ObservableList<String> getSpecialCategories() { return specialCategories; }
    public void setSpecialCategories(List<String> categories) {
        specialCategories.clear();
        specialCategories.addAll(categories);
    }
    public String getSpecialCategoryString() {
        return String.join(",", specialCategories);
    }

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

    public String getCategoryColor(String category) {
        return switch (category) {
            case "K" -> "#FF6B6B"; // Red for Commercial
            case "S" -> "#4ECDC4"; // Teal for Express
            case "M" -> "#45B7D1"; // Blue for Night
            default -> "#95A5A6"; // Grey for unknown
        };
    }

    public String getCategoryTextColor(String category) {
        return switch (category) {
            case "K" -> "#721C24"; // Dark red
            case "S" -> "#0E6251"; // Dark teal
            case "M" -> "#1B4F72"; // Dark blue
            default -> "#34495E"; // Dark grey
        };
    }
}