package su.pank.transport.ui.route;

import su.pank.transport.data.models.Category;
import su.pank.transport.data.models.Route;
import su.pank.transport.data.models.RoutePoint;
import su.pank.transport.data.repository.RouteRepository;
import su.pank.transport.data.repository.RoutePointRepository;
import su.pank.transport.domain.LinkedList;

/**
 * ViewModel для диалога маршрута
 */
public class RouteDialogViewModel {
    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;

    public RouteDialogViewModel(RouteRepository routeRepository, RoutePointRepository routePointRepository) {
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
    }

    public Category[] getAllCategories() {
        return routeRepository.getAllCategories();
    }

    public RoutePoint[] getAllRoutePoints() {
        return routePointRepository.getAllRoutePoints();
    }

    public boolean addRoute(Route route) {
        return routeRepository.addRoute(route);
    }

    public boolean updateRoute(Route route) {
        return routeRepository.updateRoute(route);
    }

    // Валидация Route
    public boolean validateAndSaveRoute(String numText, RoutePoint start, RoutePoint end, String[] selectedCategories, Route existingRoute) {
        try {
            int routeNum = Integer.parseInt(numText);
            if (routeNum < 1 || routeNum > 999) {
                return false;
            }

            if (start == null || end == null) {
                return false;
            }

            Route route = new Route(existingRoute != null ? existingRoute.getId() : 0, routeNum,
                    start.getId(), start.getLocality(), start.getDistrict(), start.getDescription(),
                    end.getId(), end.getLocality(), end.getDistrict(), end.getDescription(),
                    String.join(",", selectedCategories));

            if (existingRoute == null) {
                return addRoute(route);
            } else {
                return updateRoute(route);
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
}