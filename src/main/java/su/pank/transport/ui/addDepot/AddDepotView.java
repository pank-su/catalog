package su.pank.transport.ui.addDepot;

import su.pank.transport.data.models.RoutePoint;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;


/**
 * View для добавления нового депо
 */
public class AddDepotView extends Dialog<RoutePoint> {

    public AddDepotView(AddDepotViewModel viewModel) {

        setTitle("Добавить депо");
        setHeaderText("Введите данные депо");

        ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

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

        getDialogPane().setContent(grid);

        Platform.runLater(localityField::requestFocus);

        setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String locality = localityField.getText();
                String district = districtField.getText();
                String description = descField.getText();
                
                String errorMsg = viewModel.validateAndAddRoutePoint(locality, district, description);
                if (errorMsg == null) {
                    // Успешно добавлено
                    return new RoutePoint(0, locality.trim(), district.trim(), description.trim());
                } else {
                    // Показываем ошибку
                    showAlert("Ошибка", errorMsg);
                    return null;
                }
            }
            return null;
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}