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

    public String validateAndAddRoutePoint(String locality, String district, String description) {
        // Проверка на пустые поля
        if (locality == null || locality.trim().isEmpty() || 
            district == null || district.trim().isEmpty() || 
            description == null || description.trim().isEmpty()) {
            return "Все поля должны быть заполнены.";
        }

        RoutePoint point = new RoutePoint(0, locality.trim(), district.trim(), description.trim());
        boolean success = addRoutePoint(point);
        
        if (success) {
            return null; // null означает успех
        } else {
            return "Не удалось добавить депо. Попробуйте еще раз.";
        }
    }
}