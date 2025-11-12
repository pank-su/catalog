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
    public String validateAndSaveRoute(String numText, RoutePoint start, RoutePoint end, String[] selectedCategories, Route existingRoute) {
        // Проверка на пустое поле ID
        if (numText == null || numText.trim().isEmpty()) {
            return "Номер маршрута не может быть пустым.";
        }

        // Проверка на корректность числа
        int routeNum;
        try {
            routeNum = Integer.parseInt(numText.trim());
        } catch (NumberFormatException e) {
            return "Номер маршрута должен быть числом от 1 до 999.";
        }

        // Проверка диапазона
        if (routeNum < 1 || routeNum > 999) {
            return "Номер маршрута должен быть числом от 1 до 999.";
        }

        // Проверка выбора пунктов
        if (start == null || end == null) {
            return "Необходимо выбрать начальный и конечный пункты.";
        }

        // Проверка на дублирующийся ID (только для новых маршрутов)
        if (existingRoute == null && isRouteNumberExists(routeNum)) {
            return "Маршрут с таким номером уже существует.";
        }

        // Проверка на дублирующийся ID при редактировании (если номер изменился)
        if (existingRoute != null && existingRoute.getRouteNumber() != routeNum && isRouteNumberExists(routeNum)) {
            return "Маршрут с таким номером уже существует.";
        }

        Route route = new Route(existingRoute != null ? existingRoute.getId() : 0, routeNum,
                start.getId(), start.getLocality(), start.getDistrict(), start.getDescription(),
                end.getId(), end.getLocality(), end.getDistrict(), end.getDescription(),
                String.join(",", selectedCategories));

        boolean success;
        if (existingRoute == null) {
            success = addRoute(route);
        } else {
            success = updateRoute(route);
        }

        if (success) {
            return null; // null означает успех
        } else {
            return "Не удалось сохранить маршрут. Попробуйте еще раз.";
        }
    }

    private boolean isRouteNumberExists(int routeNumber) {
        return routeRepository.isRouteNumberExists(routeNumber);
    }
}