package su.pank.transport.ui.main;

import su.pank.transport.data.repository.RoutePointRepository;
import su.pank.transport.data.repository.RouteRepository;
import su.pank.transport.data.models.Route;
import su.pank.transport.domain.RouteLinkedList;
import su.pank.transport.domain.SimpleLinkedList;
import su.pank.transport.domain.LinkedList;
import su.pank.transport.data.models.RoutePoint;
import su.pank.transport.data.models.Category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class MainViewModel {
    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;

    private final RouteLinkedList routes;
    private final ObservableList<RouteUI> observableRoutes;

    public MainViewModel(RouteRepository routeRepository, RoutePointRepository routePointRepository) {
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;

        this.routes = new RouteLinkedList();
        this.observableRoutes = FXCollections.observableArrayList();
    }

    public void initialize() {
        routeRepository.initialize();
        routePointRepository.initialize();
        loadAllRoutes();
    }

    public void loadAllRoutes() {
        routes.clear();
        RouteLinkedList dbRoutes = routeRepository.getAllRoutes();

        observableRoutes.clear();
        for (int i = 0; i < dbRoutes.size(); i++) {
            Route route = dbRoutes.get(i);
            routes.add(route);
            observableRoutes.add(new RouteUI(route));
        }
    }

    public ObservableList<RouteUI> getObservableRoutes() {
        return observableRoutes;
    }

    public boolean addRoute(Route route) {
        if (routeRepository.addRoute(route)) {
            loadAllRoutes();
            return true;
        }
        return false;
    }

    public boolean updateRoute(Route route) {
        if (routeRepository.updateRoute(route)) {
            loadAllRoutes();
            return true;
        }
        return false;
    }

    public boolean deleteRoute(Route route) {
        if (routeRepository.deleteRoute(route.getId())) {
            routes.remove(route);
            observableRoutes.removeIf(rpm -> rpm.getRoute().getId() == route.getId());
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
        Route[] routeArray = routes.toArray();
        RouteUI[] routeUIArray = new RouteUI[routeArray.length];
        for (int i = 0; i < routeArray.length; i++) {
            routeUIArray[i] = new RouteUI(routeArray[i]);
        }
        observableRoutes.addAll(routeUIArray);
    }

    public void exportToCSV(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID,Route Number,Start Point,End Point,Special Category,Route Type\n");
            for (int i = 0; i < routes.size(); i++) {
                Route r = routes.get(i);
                writer.write(String.format("%d,%d,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        r.getId(),
                        r.getRouteNumber(),
                        r.getStartDescription() + " (" + r.getStartLocality() + ", " + r.getStartDistrict() + ")",
                        r.getEndDescription() + " (" + r.getEndLocality() + ", " + r.getEndDistrict() + ")",
                        r.getSpecialCategoryString(),
                        r.getRouteType()
                ));
            }
        }
    }

    public void importFromCSV(File file) throws IOException {
        LinkedList<RoutePoint> allPoints = new SimpleLinkedList<>(RoutePoint.class);
        for (RoutePoint p : getAllRoutePoints()) {
            allPoints.add(p);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1); // Split CSV with quotes
                if (parts.length < 6) continue;
                try {
                    int routeNumber = Integer.parseInt(parts[1].trim());
                    String startPointStr = parts[2].replaceAll("^\"|\"$", "").trim();
                    String endPointStr = parts[3].replaceAll("^\"|\"$", "").trim();
                    String specialCategory = parts[4].replaceAll("^\"|\"$", "").trim();

                    RoutePoint startPoint = findOrCreateRoutePoint(startPointStr, allPoints);
                    RoutePoint endPoint = findOrCreateRoutePoint(endPointStr, allPoints);

                    Route route = new Route(0, routeNumber, startPoint.getId(), startPoint.getLocality(),
                            startPoint.getDistrict(), startPoint.getDescription(),
                            endPoint.getId(), endPoint.getLocality(), endPoint.getDistrict(),
                            endPoint.getDescription(), specialCategory);
                    if (!addRoute(route)) {
                        // Route with this number already exists, skip
                    }
                } catch (Exception e) {
                    // Skip invalid lines
                }
            }
        }
        loadAllRoutes(); // Refresh the list
    }

    private RoutePoint findOrCreateRoutePoint(String pointStr, LinkedList<RoutePoint> allPoints) {
        // Parse "description (locality, district)"
        String description = pointStr;
        String locality = "";
        String district = "";
        if (pointStr.contains("(") && pointStr.contains(")")) {
            int openParen = pointStr.lastIndexOf("(");
            int closeParen = pointStr.lastIndexOf(")");
            if (openParen < closeParen) {
                description = pointStr.substring(0, openParen).trim();
                String locDist = pointStr.substring(openParen + 1, closeParen);
                String[] locDistParts = locDist.split(",");
                if (locDistParts.length >= 2) {
                    locality = locDistParts[0].trim();
                    district = locDistParts[1].trim();
                }
            }
        }

        // Find existing
        for (RoutePoint p : allPoints.toArray()) {
            if (p.getDescription().equals(description)) {
                return p;
            }
        }

        // Create new
        RoutePoint newPoint = new RoutePoint(0, locality, district, description);
        addRoutePoint(newPoint);
        allPoints.add(newPoint);
        return newPoint;
    }

    public RoutePoint[] getAllRoutePoints() {
        return routePointRepository.getAllRoutePoints();
    }

    public boolean addRoutePoint(RoutePoint point) {
        return routePointRepository.addRoutePoint(point);
    }

    public boolean deleteRoutePoint(RoutePoint point) {
        return routePointRepository.deleteRoutePoint(point.getId());
    }

    public Category[] getAllCategories() {
        return routeRepository.getAllCategories();
    }

    public RouteRepository getRouteRepository() {
        return routeRepository;
    }

    public RoutePointRepository getRoutePointRepository() {
        return routePointRepository;
    }
}