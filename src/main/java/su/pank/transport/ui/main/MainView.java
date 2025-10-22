package su.pank.transport.ui.main;

import su.pank.transport.data.models.Route;
import su.pank.transport.ui.route.RouteView;
import su.pank.transport.ui.route.RouteDialogViewModel;
import su.pank.transport.ui.depots.DepotsView;
import su.pank.transport.ui.depots.DepotsViewModel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Главный View приложения управления маршрутами транспорта
 */
public class MainView {
    private final MainViewModel viewModel;
    private final Stage primaryStage;
    private TableView<RouteUI> tableView;

    public MainView(MainViewModel viewModel, Stage primaryStage) {
        this.viewModel = viewModel;
        this.primaryStage = primaryStage;
    }

    public void show() {
        Scene scene = new Scene(createMainLayout(), 640, 480);
        scene.getStylesheets().add("data:text/css," + getCSS());

        primaryStage.setTitle("Каталог маршрутов");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            // Close repositories if needed
        });
        primaryStage.show();
    }

    private VBox createMainLayout() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #F5FAFB;");

        // Панель приложения
        HBox appBar = createAppBar();

        // Таблица
        tableView = createRouteTable();
        tableView.setItems(viewModel.getObservableRoutes());

        // Кнопки
        HBox buttonBar = createButtonBar();

        VBox content = new VBox(10, tableView, buttonBar);
        content.setPadding(new Insets(32));

        root.getChildren().addAll(appBar, content);
        VBox.setVgrow(content, Priority.ALWAYS);

        return root;
    }

    private HBox createAppBar() {
        HBox appBar = new HBox();
        appBar.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 8 4;");
        appBar.setAlignment(Pos.CENTER);
        appBar.setPrefHeight(64);

        Label title = new Label("Каталог маршрутов");
        title.setFont(Font.font("Roboto", FontWeight.NORMAL, 22));
        title.setStyle("-fx-text-fill: #161D1D;");
        HBox.setHgrow(title, Priority.ALWAYS);
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);

        Button searchBtn = createIconButton("🔍");
        searchBtn.setOnAction(e -> showSearchDialog());

        Button addBtn = createIconButton("+");
        addBtn.setOnAction(e -> showAddRouteDialog());

        HBox trailing = new HBox(searchBtn, addBtn);

        appBar.getChildren().addAll(new Region(), title, trailing);

        return appBar;
    }

    private Button createIconButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-font-size: 18; -fx-padding: 10;");
        btn.setPrefSize(48, 48);
        return btn;
    }

    private TableView<RouteUI> createRouteTable() {
        TableView<RouteUI> table = new TableView<>();
        table.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        // ID Column with colored badge and categories
        TableColumn<RouteUI, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("routeNumber"));
        idCol.setPrefWidth(120);
        idCol.setCellFactory(col -> new TableCell<RouteUI, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    RouteUI route = getTableView().getItems().get(getIndex());
                    HBox hbox = new HBox(4);
                    hbox.setAlignment(Pos.CENTER);

                    Label numberBadge = new Label(item.toString());
                    numberBadge.setStyle(String.format(
                            "-fx-background-color: %s; -fx-background-radius: 33; " +
                                    "-fx-padding: 0 8; -fx-text-fill: %s; " +
                                    "-fx-font-family: 'Roboto'; -fx-font-weight: 500; -fx-font-size: 11;",
                            route.getBadgeColor(), route.getTextColor()
                    ));

                    hbox.getChildren().add(numberBadge);

                    for (String cat : route.getSpecialCategories()) {
                        Label catBadge = new Label(cat);
                        catBadge.setStyle(String.format(
                                "-fx-background-color: %s; -fx-background-radius: 33; " +
                                        "-fx-padding: 0 6; -fx-text-fill: %s; " +
                                        "-fx-font-family: 'Roboto'; -fx-font-weight: 500; -fx-font-size: 9;",
                                route.getCategoryColor(cat), route.getCategoryTextColor(cat)
                        ));
                        Tooltip tooltip = new Tooltip(getCategoryName(cat));
                        Tooltip.install(catBadge, tooltip);
                        hbox.getChildren().add(catBadge);
                    }

                    setGraphic(hbox);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Start Point Column
        TableColumn<RouteUI, su.pank.transport.data.models.RoutePoint> startCol = new TableColumn<>("Начальный пункт");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startPoint"));
        startCol.setCellFactory(col -> new TableCell<RouteUI, su.pank.transport.data.models.RoutePoint>() {
            @Override
            protected void updateItem(su.pank.transport.data.models.RoutePoint item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label descLabel = new Label(item.getDescription());
                    Label locLabel = new Label(" (" + item.getLocality() + ", " + item.getDistrict() + ")");
                    locLabel.setStyle("-fx-text-fill: gray;");
                    HBox hbox = new HBox(descLabel, locLabel);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
                setAlignment(Pos.CENTER);
            }
        });

        // End Point Column
        TableColumn<RouteUI, su.pank.transport.data.models.RoutePoint> endCol = new TableColumn<>("Конечный пункт");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endPoint"));
        endCol.setCellFactory(col -> new TableCell<RouteUI, su.pank.transport.data.models.RoutePoint>() {
            @Override
            protected void updateItem(su.pank.transport.data.models.RoutePoint item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label descLabel = new Label(item.getDescription());
                    Label locLabel = new Label(" (" + item.getLocality() + ", " + item.getDistrict() + ")");
                    locLabel.setStyle("-fx-text-fill: gray;");
                    HBox hbox = new HBox(descLabel, locLabel);
                    hbox.setAlignment(Pos.CENTER);
                    setGraphic(hbox);
                }
                setAlignment(Pos.CENTER);
            }
        });

        table.getColumns().addAll(idCol, startCol, endCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // Обработчик клика по строке для редактирования
        table.setRowFactory(tv -> {
            TableRow<RouteUI> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showEditRouteDialog(row.getItem().getRoute());
                }
            });
            return row;
        });

        return table;
    }

    private HBox createButtonBar() {
        Button sortBtn = new Button("Сортировать");
        sortBtn.setOnAction(e -> {
            viewModel.sortByRouteNumber();
            showAlert("Сортировка", "Маршруты отсортированы по номеру");
        });

        Button deleteBtn = new Button("Удалить");
        deleteBtn.setOnAction(e -> {
            RouteUI selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Удалить маршрут " + selected.getRouteNumber() + "?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        viewModel.deleteRoute(selected.getRoute());
                    }
                });
            }
        });

        Button exportBtn = new Button("Экспорт CSV");
        exportBtn.setOnAction(e -> exportToCSV());

        Button importBtn = new Button("Импорт CSV");
        importBtn.setOnAction(e -> importFromCSV());

        Button depotsBtn = new Button("Управление депо");
        depotsBtn.setOnAction(e -> showDepotsDialog());

        HBox bar = new HBox(10, sortBtn, deleteBtn, exportBtn, importBtn, depotsBtn);
        bar.setAlignment(Pos.CENTER);
        return bar;
    }

    private void showSearchDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Поиск маршрута");
        dialog.setHeaderText("Введите номер маршрута");
        dialog.setContentText("Номер:");

        dialog.showAndWait().ifPresent(input -> {
            try {
                int routeNumber = Integer.parseInt(input);
                Route found = viewModel.searchByRouteNumber(routeNumber);
                if (found != null) {
                    showAlert("Найден маршрут", String.format(
                            "Маршрут №%d\nТип: %s\nОт: %s\nДо: %s\nКатегории: %s",
                            found.getRouteNumber(),
                            found.getRouteType(),
                            found.getStartDescription(),
                            found.getEndDescription(),
                            found.getSpecialCategories().length == 0 ? "Нет" : String.join(", ", found.getSpecialCategories())
                    ));
                } else {
                    showAlert("Не найдено", "Маршрут с номером " + routeNumber + " не найден");
                }
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Введите корректный номер маршрута");
            }
        });
    }

    private void showAddRouteDialog() {
        RouteDialogViewModel routeVM = new RouteDialogViewModel(viewModel.getRouteRepository(), viewModel.getRoutePointRepository());
        RouteView dialog = new RouteView(routeVM, null);
        dialog.showAndWait();
        viewModel.loadAllRoutes(); // Refresh main view
    }

    private void showEditRouteDialog(Route route) {
        RouteDialogViewModel routeVM = new RouteDialogViewModel(viewModel.getRouteRepository(), viewModel.getRoutePointRepository());
        RouteView dialog = new RouteView(routeVM, route);
        dialog.showAndWait();
        viewModel.loadAllRoutes(); // Refresh main view
    }

    private void showDepotsDialog() {
        DepotsViewModel depotsVM = new DepotsViewModel(viewModel.getRoutePointRepository());
        DepotsView dialog = new DepotsView(depotsVM);
        dialog.showAndWait();
    }

    private void exportToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Экспорт в CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файлы", "*.csv")
        );
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            try {
                viewModel.exportToCSV(file);
                showAlert("Успех", "Данные экспортированы в " + file.getName());
            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось экспортировать данные: " + e.getMessage());
            }
        }
    }

    private void importFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Импорт из CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV файлы", "*.csv")
        );
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                viewModel.importFromCSV(file);
                showAlert("Успех", "Данные импортированы из " + file.getName());
            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось импортировать данные: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String getCategoryName(String code) {
        return switch (code) {
            case "K" -> "Коммерческий";
            case "S" -> "Экспресс";
            case "M" -> "Ночной";
            default -> "";
        };
    }

    private String getCSS() {
        return """
                    .table-view {
                        -fx-background-color: white;
                    }
                    .table-view .column-header {
                        -fx-background-color: #E3E9EA;
                        -fx-text-fill: black;
                    }
                    .button {
                        -fx-background-color: #CCE8EA;
                        -fx-text-fill: black;
                        -fx-padding: 8 16;
                        -fx-background-radius: 6;
                    }
                    .button:hover {
                        -fx-background-color: #B0D4D7;
                    }
                """.replaceAll("\n", "");
    }
}