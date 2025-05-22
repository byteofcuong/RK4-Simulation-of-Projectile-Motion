package com.npc.rk4.Views;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewFactory {
    private HBox menu;

    //one graphs view
    private AnchorPane oneGraphsView;

    //
    private ObjectProperty<MenuOptions> selectedMenuItem;

    public ViewFactory() {
        selectedMenuItem = new SimpleObjectProperty<>();
    }

    public ObjectProperty<MenuOptions> getSelectedMenuItem() {
        return selectedMenuItem;
    }

    //load giao dien
    public HBox getMenu() {
        if (menu == null) {
            try {
                menu = new FXMLLoader(getClass().getResource("/FXML/Menu.fxml")).load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return menu;
    }

    public AnchorPane getOneGraphsView() {
        if (oneGraphsView == null) {
            try {
                oneGraphsView = new FXMLLoader(getClass().getResource("/FXML/OneGraphs.fxml")).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return oneGraphsView;
    }

    //show giao dien
    public void showAppWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/App.fxml"));
        createStage(loader);
    }

    private void createStage(FXMLLoader loader) {
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        //stage.getIcons().add(new Image(String.valueOf(getClass().getResource(""))));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("RK4");

        stage.setOnCloseRequest(event -> {     // khong nhan su kien khi goi stage.close()
            try {
                event.consume();
                exit(stage);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        stage.show();
    }

    public void exit(Stage stage) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close Window");
        alert.setHeaderText("Confirm to exit program");
        alert.setContentText("Do you want to exit ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Exit successfully");
            stage.close();
        }
    }
}
