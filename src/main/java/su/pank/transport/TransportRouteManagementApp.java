package su.pank.transport;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import su.pank.transport.db.DatabaseManager;
import su.pank.transport.model.Route;
import su.pank.transport.model.RoutePoint;
import su.pank.transport.viewmodel.RouteViewModel;

/**
 * Public Transport Route Management Desktop Application
 * MVVM Architecture with JavaFX + SQLite
 */
public class TransportRouteManagementApp extends Application {

    private DatabaseManager dbManager;
    private RouteViewModel viewModel;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Initialize database
        dbManager = new DatabaseManager();
        dbManager.initialize();

        // Initialize ViewModel
        viewModel = new RouteViewModel(dbManager);
        viewModel.loadAllRoutes();

        // Create main view
        Scene scene = new Scene(createMainView(), 640, 480);
        scene.getStylesheets().add("data:text/css," + getCSS());

        stage.setTitle("Каталог маршрутов");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> dbManager.close());
        stage.show();
    }




    // ==================== VIEWS ====================

    private VBox createMainView() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #F5FAFB;");

        // App Bar
        HBox appBar = createAppBar();

        // Table
        TableView<Route> tableView = createRouteTable();
        tableView.setItems(viewModel.getObservableRoutes());

        // Buttons
        HBox buttonBar = createButtonBar(tableView);

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

        // Trailing buttons
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

    private TableView<Route> createRouteTable() {
        TableView<Route> table = new TableView<>();
        table.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        // ID Column with colored badge and categories
        TableColumn<Route, Integer> idCol = new TableColumn<>("Id");
        idCol.setCellValueFactory(new PropertyValueFactory<>("routeNumber"));
        idCol.setPrefWidth(120);
        idCol.setCellFactory(col -> new TableCell<Route, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Route route = getTableView().getItems().get(getIndex());
                    HBox hbox = new HBox(4);
                    hbox.setAlignment(Pos.CENTER);

                    // Route number badge
                    Label numberBadge = new Label(item.toString());
                    numberBadge.setStyle(String.format(
                            "-fx-background-color: %s; -fx-background-radius: 33; " +
                                    "-fx-padding: 0 8; -fx-text-fill: %s; " +
                                    "-fx-font-family: 'Roboto'; -fx-font-weight: 500; -fx-font-size: 11;",
                            route.getBadgeColor(), route.getTextColor()
                    ));

                    hbox.getChildren().add(numberBadge);

                    // Category badges
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
        TableColumn<Route, RoutePoint> startCol = new TableColumn<>("Начальный пункт");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startPoint"));
        startCol.setCellFactory(col -> new TableCell<Route, RoutePoint>() {
            @Override
            protected void updateItem(RoutePoint item, boolean empty) {
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
        TableColumn<Route, RoutePoint> endCol = new TableColumn<>("Конечный пункт");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endPoint"));
        endCol.setCellFactory(col -> new TableCell<Route, RoutePoint>() {
            @Override
            protected void updateItem(RoutePoint item, boolean empty) {
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
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Row click handler for edit
        table.setRowFactory(tv -> {
            TableRow<Route> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showEditRouteDialog(row.getItem());
                }
            });
            return row;
        });

        return table;
    }

    private HBox createButtonBar(TableView<Route> table) {
        Button sortBtn = new Button("Сортировать");
        sortBtn.setOnAction(e -> {
            viewModel.sortByRouteNumber();
            showAlert("Сортировка", "Маршруты отсортированы по номеру");
        });

        Button deleteBtn = new Button("Удалить");
        deleteBtn.setOnAction(e -> {
            Route selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Удалить маршрут " + selected.getRouteNumber() + "?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        viewModel.deleteRoute(selected);
                    }
                });
            }
        });

        Button exportBtn = new Button("Экспорт CSV");
        exportBtn.setOnAction(e -> exportToCSV());

        Button depotsBtn = new Button("Управление депо");
        depotsBtn.setOnAction(e -> showDepotsDialog());

        HBox bar = new HBox(10, sortBtn, deleteBtn, exportBtn, depotsBtn);
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
                            found.getStartPoint().getDescription(),
                            found.getEndPoint().getDescription(),
                            found.getSpecialCategories().isEmpty() ? "Нет" : String.join(", ", found.getSpecialCategories())
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
        showRouteDialog(null);
    }

    private void showEditRouteDialog(Route route) {
        showRouteDialog(route);
    }

    private void showRouteDialog(Route existingRoute) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(existingRoute == null ? "Добавить маршрут" : "Редактировать маршрут");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(32));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: #F5FAFB;");

        // Route Number
        Label numLabel = new Label("ID*");
        TextField numField = new TextField();
        numField.setPromptText("1-999");
        numField.setStyle("-fx-background-color: #CCE8EA; -fx-background-radius: 6;");
        if (existingRoute != null) {
            numField.setText(String.valueOf(existingRoute.getRouteNumber()));
        }

        // Categories
        Label catLabel = new Label("Категории");
        VBox catBox = new VBox(5);
        CheckBox kCheck = new CheckBox("K - Коммерческий");
        CheckBox sCheck = new CheckBox("S - Экспресс");
        CheckBox mCheck = new CheckBox("M - Ночной");
        catBox.getChildren().addAll(kCheck, sCheck, mCheck);
        if (existingRoute != null) {
            for (String cat : existingRoute.getSpecialCategories()) {
                switch (cat) {
                    case "K" -> kCheck.setSelected(true);
                    case "S" -> sCheck.setSelected(true);
                    case "M" -> mCheck.setSelected(true);
                }
            }
        }

        // Depot ComboBoxes
        List<RoutePoint> depots = viewModel.getAllRoutePoints();

        ComboBox<RoutePoint> startCombo = new ComboBox<>();
        startCombo.getItems().addAll(depots);
        startCombo.setStyle("-fx-background-color: #CCE8EA; -fx-background-radius: 6; -fx-pref-width: 280;");

        ComboBox<RoutePoint> endCombo = new ComboBox<>();
        endCombo.getItems().addAll(depots);
        endCombo.setStyle("-fx-background-color: #CCE8EA; -fx-background-radius: 6; -fx-pref-width: 280;");

        if (existingRoute != null) {
            startCombo.setValue(existingRoute.getStartPoint());
            endCombo.setValue(existingRoute.getEndPoint());
        }

        // Layout
        int row = 0;
        grid.add(numLabel, 0, row);
        grid.add(numField, 1, row);
        grid.add(catLabel, 2, row);
        grid.add(catBox, 3, row);

        row++;
        grid.add(new Label("Начальный пункт"), 0, row, 2, 1);
        grid.add(new Label("Конечный пункт"), 2, row, 2, 1);

        row++;
        grid.add(startCombo, 0, row, 2, 1);
        grid.add(endCombo, 2, row, 2, 1);

        // Buttons
        Button saveBtn = new Button(existingRoute == null ? "Добавить" : "Сохранить");
        Button cancelBtn = new Button("Отмена");

        saveBtn.setOnAction(e -> {
            try {
                int routeNum = Integer.parseInt(numField.getText());
                if (routeNum < 1 || routeNum > 999) {
                    showAlert("Ошибка", "Номер маршрута должен быть от 1 до 999");
                    return;
                }

                RoutePoint start = startCombo.getValue();
                RoutePoint end = endCombo.getValue();

                if (start == null || end == null) {
                    showAlert("Ошибка", "Выберите начальный и конечный пункты");
                    return;
                }

                List<String> categories = new ArrayList<>();
                if (kCheck.isSelected()) categories.add("K");
                if (sCheck.isSelected()) categories.add("S");
                if (mCheck.isSelected()) categories.add("M");

                if (existingRoute == null) {
                    Route newRoute = new Route(0, routeNum, start, end, String.join(",", categories));
                    if (viewModel.addRoute(newRoute)) {
                        dialog.close();
                    } else {
                        showAlert("Ошибка", "Не удалось добавить маршрут. Возможно, такой номер уже существует.");
                    }
                } else {
                    existingRoute.setRouteNumber(routeNum);
                    existingRoute.setStartPoint(start);
                    existingRoute.setEndPoint(end);
                    existingRoute.setSpecialCategories(categories);
                    if (viewModel.updateRoute(existingRoute)) {
                        dialog.close();
                    } else {
                        showAlert("Ошибка", "Не удалось обновить маршрут");
                    }
                }
            } catch (NumberFormatException ex) {
                showAlert("Ошибка", "Введите корректный номер маршрута");
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        HBox btnBox = new HBox(10, saveBtn, cancelBtn);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 0, row + 1, 4, 1);

        Scene scene = new Scene(grid, 640, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private String getCategoryName(String code) {
        return switch (code) {
            case "K" -> "Коммерческий";
            case "S" -> "Экспресс";
            case "M" -> "Ночной";
            default -> "";
        };
    }

    private void showDepotsDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Управление депо");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        ListView<RoutePoint> listView = new ListView<>();
        listView.getItems().addAll(viewModel.getAllRoutePoints());
        listView.setPrefHeight(300);

        Button addBtn = new Button("Добавить депо");
        addBtn.setOnAction(e -> {
            showAddDepotDialog();
            listView.getItems().clear();
            listView.getItems().addAll(viewModel.getAllRoutePoints());
        });

        Button deleteBtn = new Button("Удалить");
        deleteBtn.setOnAction(e -> {
            RoutePoint selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Удалить депо?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        viewModel.deleteRoutePoint(selected);
                        listView.getItems().remove(selected);
                    }
                });
            }
        });

        Button closeBtn = new Button("Закрыть");
        closeBtn.setOnAction(e -> dialog.close());

        HBox btnBox = new HBox(10, addBtn, deleteBtn, closeBtn);
        btnBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(new Label("Депо:"), listView, btnBox);

        Scene scene = new Scene(root, 500, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAddDepotDialog() {
        Dialog<RoutePoint> dialog = new Dialog<>();
        dialog.setTitle("Добавить депо");
        dialog.setHeaderText("Введите данные депо");

        ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField localityField = new TextField();
        localityField.setPromptText("Населённый пункт");
        TextField districtField = new TextField();
        districtField.setPromptText("Район");
        TextField descField = new TextField();
        descField.setPromptText("Описание");

        grid.add(new Label("Населённый пункт*:"), 0, 0);
        grid.add(localityField, 1, 0);
        grid.add(new Label("Район*:"), 0, 1);
        grid.add(districtField, 1, 1);
        grid.add(new Label("Описание*:"), 0, 2);
        grid.add(descField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> localityField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new RoutePoint(0, localityField.getText(),
                        districtField.getText(), descField.getText());
            }
            return null;
        });

        Optional<RoutePoint> result = dialog.showAndWait();
        result.ifPresent(point -> {
            if (point.getLocality().isEmpty() || point.getDistrict().isEmpty() ||
                    point.getDescription().isEmpty()) {
                showAlert("Ошибка", "Все поля обязательны");
            } else {
                viewModel.addRoutePoint(point);
            }
        });
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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

    public static void main(String[] args) {
        launch(args);
    }
}