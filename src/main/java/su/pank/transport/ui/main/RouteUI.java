package su.pank.transport.ui.main;

import su.pank.transport.data.models.Route;
import su.pank.transport.data.models.RoutePoint;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class RouteUI {
    private final Route route;
    private final IntegerProperty id;
    private final IntegerProperty routeNumber;
    private final ObjectProperty<RoutePoint> startPoint;
    private final ObjectProperty<RoutePoint> endPoint;
    private final ObservableList<String> specialCategories;
    private final StringProperty routeType;

    public RouteUI(Route route) {
        this.route = route;
        this.id = new SimpleIntegerProperty(route.getId());
        this.routeNumber = new SimpleIntegerProperty(route.getRouteNumber());
        this.startPoint = new SimpleObjectProperty<>(new RoutePoint(route.getStartPointId(),
                route.getStartLocality(), route.getStartDistrict(), route.getStartDescription()));
        this.endPoint = new SimpleObjectProperty<>(new RoutePoint(route.getEndPointId(),
                route.getEndLocality(), route.getEndDistrict(), route.getEndDescription()));
        this.specialCategories = FXCollections.observableArrayList(route.getSpecialCategories());
        this.routeType = new SimpleStringProperty(route.getRouteType());
    }

    public Route getRoute() {
        return route;
    }

    public int getId() { return id.get(); }
    public int getRouteNumber() { return routeNumber.get(); }
    public void setRouteNumber(int value) {
        routeNumber.set(value);
        route.setRouteNumber(value);
        routeType.set(route.getRouteType());
    }
    public IntegerProperty routeNumberProperty() { return routeNumber; }

    public RoutePoint getStartPoint() { return startPoint.get(); }
    public void setStartPoint(RoutePoint value) {
        startPoint.set(value);
        route.setStartPointId(value.getId());
        route.setStartLocality(value.getLocality());
        route.setStartDistrict(value.getDistrict());
        route.setStartDescription(value.getDescription());
    }
    public ObjectProperty<RoutePoint> startPointProperty() { return startPoint; }

    public RoutePoint getEndPoint() { return endPoint.get(); }
    public void setEndPoint(RoutePoint value) {
        endPoint.set(value);
        route.setEndPointId(value.getId());
        route.setEndLocality(value.getLocality());
        route.setEndDistrict(value.getDistrict());
        route.setEndDescription(value.getDescription());
    }
    public ObjectProperty<RoutePoint> endPointProperty() { return endPoint; }

    public ObservableList<String> getSpecialCategories() { return specialCategories; }
    public void setSpecialCategories(java.util.List<String> categories) {
        specialCategories.clear();
        specialCategories.addAll(categories);
        route.setSpecialCategories(categories);
    }

    public String getRouteType() { return routeType.get(); }
    public StringProperty routeTypeProperty() { return routeType; }

    // Delegate UI methods to route
    public String getBadgeColor() { return route.getBadgeColor(); }
    public String getTextColor() { return route.getTextColor(); }
    public String getCategoryColor(String category) { return route.getCategoryColor(category); }
    public String getCategoryTextColor(String category) { return route.getCategoryTextColor(category); }
}