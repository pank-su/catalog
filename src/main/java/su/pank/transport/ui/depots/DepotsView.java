package su.pank.transport.ui.depots;

import su.pank.transport.data.models.RoutePoint;
import su.pank.transport.ui.addDepot.AddDepotView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * View для управления депо (точками маршрутов)
 */
public class DepotsView extends Stage {
    private final DepotsViewModel viewModel;
    private ListView<RoutePoint> listView;

    public DepotsView(DepotsViewModel viewModel) {
        this.viewModel = viewModel;

        initModality(Modality.APPLICATION_MODAL);
        setTitle("Управление депо");

        Scene scene = new Scene(createContent(), 500, 400);
        setScene(scene);
    }

    private VBox createContent() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        listView = new ListView<>();
        listView.getItems().addAll(viewModel.getAllRoutePoints());
        listView.setPrefHeight(300);

        Button addBtn = new Button("Добавить депо");
        addBtn.setOnAction(e -> {
            AddDepotView dialog = new AddDepotView(viewModel.getAddDepotViewModel());
            dialog.showAndWait();
            refreshList();
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
        closeBtn.setOnAction(e -> close());

        HBox btnBox = new HBox(10, addBtn, deleteBtn, closeBtn);
        btnBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(new Label("Депо:"), listView, btnBox);

        return root;
    }

    private void refreshList() {
        listView.getItems().clear();
        listView.getItems().addAll(viewModel.getAllRoutePoints());
    }
}