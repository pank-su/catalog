package su.pank.transport.ui.depots;

import su.pank.transport.data.models.RoutePoint;
import su.pank.transport.data.repository.RoutePointRepository;
import su.pank.transport.ui.addDepot.AddDepotViewModel;

import java.util.List;

/**
 * ViewModel для управления депо
 */
public class DepotsViewModel {
    private final RoutePointRepository routePointRepository;
    private final AddDepotViewModel addDepotViewModel;

    public DepotsViewModel(RoutePointRepository routePointRepository) {
        this.routePointRepository = routePointRepository;
        this.addDepotViewModel = new AddDepotViewModel(routePointRepository);
    }

    public List<RoutePoint> getAllRoutePoints() {
        return routePointRepository.getAllRoutePoints();
    }

    public boolean deleteRoutePoint(RoutePoint point) {
        return routePointRepository.deleteRoutePoint(point.getId());
    }

    public AddDepotViewModel getAddDepotViewModel() {
        return addDepotViewModel;
    }
}