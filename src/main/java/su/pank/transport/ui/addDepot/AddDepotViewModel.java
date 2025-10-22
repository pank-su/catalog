package su.pank.transport.ui.addDepot;

import su.pank.transport.data.models.RoutePoint;
import su.pank.transport.data.repository.RoutePointRepository;

/**
 * ViewModel для добавления нового депо
 */
public class AddDepotViewModel {
    private final RoutePointRepository routePointRepository;

    public AddDepotViewModel(RoutePointRepository routePointRepository) {
        this.routePointRepository = routePointRepository;
    }

    public boolean addRoutePoint(RoutePoint point) {
        return routePointRepository.addRoutePoint(point);
    }

    public boolean validateAndAddRoutePoint(String locality, String district, String description) {
        if (locality.isEmpty() || district.isEmpty() || description.isEmpty()) {
            return false;
        }
        RoutePoint point = new RoutePoint(0, locality, district, description);
        return addRoutePoint(point);
    }
}