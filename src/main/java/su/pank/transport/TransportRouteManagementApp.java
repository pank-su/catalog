package su.pank.transport;

import su.pank.transport.data.repository.RoutePointRepository;
import su.pank.transport.data.repository.RouteRepository;
import su.pank.transport.ui.main.MainView;
import su.pank.transport.ui.main.MainViewModel;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Десктопное приложение для управления маршрутами общественного транспорта
 * Чистая архитектура с JavaFX + SQLite
 */
public class TransportRouteManagementApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Инициализация слоя данных
        RouteRepository routeRepository = new RouteRepository();
        RoutePointRepository routePointRepository = new RoutePointRepository();

        // Инициализация viewmodel
        MainViewModel viewModel = new MainViewModel(routeRepository, routePointRepository);
        viewModel.initialize();

        // Создание и отображение главного view
        MainView mainView = new MainView(viewModel, primaryStage);
        mainView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}