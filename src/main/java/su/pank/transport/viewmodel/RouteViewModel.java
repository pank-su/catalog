package su.pank.transport.viewmodel;

import su.pank.transport.db.DatabaseManager;
import su.pank.transport.model.Route;
import su.pank.transport.model.RouteLinkedList;
import su.pank.transport.model.RoutePoint;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.List;

public class RouteViewModel {
    private final DatabaseManager dbManager;
    private final RouteLinkedList routes;
    private final ObservableList<Route> observableRoutes;

    public RouteViewModel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.routes = new RouteLinkedList();
        this.observableRoutes = FXCollections.observableArrayList();
    }

    public void loadAllRoutes() {
        routes.clear();
        RouteLinkedList dbRoutes = dbManager.getAllRoutes();

        observableRoutes.clear();
        for (int i = 0; i < dbRoutes.size(); i++) {
            Route route = dbRoutes.get(i);
            routes.add(route);
            observableRoutes.add(route);
        }
    }

    public ObservableList<Route> getObservableRoutes() {
        return observableRoutes;
    }

    public boolean addRoute(Route route) {
        if (dbManager.addRoute(route)) {
            loadAllRoutes();
            return true;
        }
        return false;
    }

    public boolean updateRoute(Route route) {
        if (dbManager.updateRoute(route)) {
            loadAllRoutes();
            return true;
        }
        return false;
    }

    public boolean deleteRoute(Route route) {
        if (dbManager.deleteRoute(route.getId())) {
            routes.remove(route);
            observableRoutes.remove(route);
            return true;
        }
        return false;
    }

    public Route searchByRouteNumber(int routeNumber) {
        return routes.linearSearch(routeNumber);
    }

    public void sortByRouteNumber() {
        routes.insertionSort();
        observableRoutes.clear();
        observableRoutes.addAll(routes.toList());
    }

    public void exportToCSV(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID,Route Number,Start Point,End Point,Special Category,Route Type\n");
            for (int i = 0; i < routes.size(); i++) {
                Route r = routes.get(i);
                writer.write(String.format("%d,%d,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        r.getId(),
                        r.getRouteNumber(),
                        r.getStartPoint().toString(),
                        r.getEndPoint().toString(),
                        r.getSpecialCategoryString(),
                        r.getRouteType()
                ));
            }
        }
    }

    public void saveToFile(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            List<Route> routeList = routes.toList();
            oos.writeInt(routeList.size());
            for (Route route : routeList) {
                oos.writeInt(route.getRouteNumber());
                oos.writeInt(route.getStartPoint().getId());
                oos.writeInt(route.getEndPoint().getId());
                oos.writeUTF(route.getSpecialCategoryString());
            }
        }
    }

    public List<RoutePoint> getAllRoutePoints() {
        return dbManager.getAllRoutePoints();
    }

    public boolean addRoutePoint(RoutePoint point) {
        return dbManager.addRoutePoint(point);
    }

    public boolean deleteRoutePoint(RoutePoint point) {
        return dbManager.deleteRoutePoint(point.getId());
    }
}