package su.pank.transport.ui.route;

import su.pank.transport.data.models.Category;
import su.pank.transport.data.models.Route;
import su.pank.transport.data.models.RoutePoint;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


/**
 * View для добавления или редактирования маршрута
 */
public class RouteView extends Stage {
    private final RouteDialogViewModel viewModel;
    private final Route existingRoute;

    public RouteView(RouteDialogViewModel viewModel, Route existingRoute) {
        this.viewModel = viewModel;
        this.existingRoute = existingRoute;

        initModality(Modality.APPLICATION_MODAL);
        setTitle(existingRoute == null ? "Добавить маршрут" : "Редактировать маршрут");

        Scene scene = new Scene(createContent(), 640, 400);
        setScene(scene);
    }

    private GridPane createContent() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(32));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-background-color: #F5FAFB;");

        // Номер маршрута
        Label numLabel = new Label("ID*");
        TextField numField = new TextField();
        numField.setPromptText("1-999");
        numField.setStyle("-fx-background-color: #CCE8EA; -fx-background-radius: 6;");
        if (existingRoute != null) {
            numField.setText(String.valueOf(existingRoute.getRouteNumber()));
        }

        Label catLabel = new Label("Категории");
        VBox catBox = new VBox(5);
        List<Category> allCategories = viewModel.getAllCategories();
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (Category cat : allCategories) {
            CheckBox checkBox = new CheckBox(cat.getCode() + " - " + cat.getName());
            checkBoxes.add(checkBox);
            catBox.getChildren().add(checkBox);
        }
        if (existingRoute != null) {
            for (String catCode : existingRoute.getSpecialCategories()) {
                for (CheckBox cb : checkBoxes) {
                    if (cb.getText().startsWith(catCode + " - ")) {
                        cb.setSelected(true);
                        break;
                    }
                }
            }
        }

        List<RoutePoint> depots = viewModel.getAllRoutePoints();

        ComboBox<RoutePoint> startCombo = new ComboBox<>();
        startCombo.getItems().addAll(depots);
        startCombo.setStyle("-fx-background-color: #CCE8EA; -fx-background-radius: 6; -fx-pref-width: 280;");

        ComboBox<RoutePoint> endCombo = new ComboBox<>();
        endCombo.getItems().addAll(depots);
        endCombo.setStyle("-fx-background-color: #CCE8EA; -fx-background-radius: 6; -fx-pref-width: 280;");

        if (existingRoute != null) {
            startCombo.setValue(new RoutePoint(existingRoute.getStartPointId(),
                    existingRoute.getStartLocality(), existingRoute.getStartDistrict(), existingRoute.getStartDescription()));
            endCombo.setValue(new RoutePoint(existingRoute.getEndPointId(),
                    existingRoute.getEndLocality(), existingRoute.getEndDistrict(), existingRoute.getEndDescription()));
        }

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

        // Кнопки
        Button saveBtn = new Button(existingRoute == null ? "Добавить" : "Сохранить");
        Button cancelBtn = new Button("Отмена");

        saveBtn.setOnAction(e -> {
            RoutePoint start = startCombo.getValue();
            RoutePoint end = endCombo.getValue();

            List<String> selectedCategories = new ArrayList<>();
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (checkBoxes.get(i).isSelected()) {
                    selectedCategories.add(allCategories.get(i).getCode());
                }
            }

            if (viewModel.validateAndSaveRoute(numField.getText(), start, end, selectedCategories, existingRoute)) {
                close();
            } else {
                String errorMsg = existingRoute == null ?
                    "Не удалось добавить маршрут. Возможно, такой номер уже существует." :
                    "Не удалось обновить маршрут";
                showAlert("Ошибка", errorMsg);
            }
        });

        cancelBtn.setOnAction(e -> close());

        HBox btnBox = new HBox(10, saveBtn, cancelBtn);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 0, row + 1, 4, 1);

        return grid;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}