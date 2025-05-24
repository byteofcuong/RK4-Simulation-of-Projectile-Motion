package com.npc.rk4.Controllers;

import com.npc.rk4.Models.TrajectoryModel;
import com.npc.rk4.Views.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public BorderPane app_parent;
    public LineChart<Number, Number> trajectoryChart;
    public LineChart<Number, Number> xTimeChart;
    public LineChart<Number, Number> yTimeChart;
    public LineChart<Number, Number> vxTimeChart;
    public LineChart<Number, Number> vyTimeChart;

    private MenuController menuController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Pane menuPane = ViewFactory.getInstance().getMenu();
        menuController = ViewFactory.getInstance().getMenuController();

        if (menuController != null) {
            menuController.setAppController(this);
        }

        if (trajectoryChart == null || xTimeChart == null || yTimeChart == null ||
            vxTimeChart == null || vyTimeChart == null) {
            System.err.println("Một số biểu đồ không được khởi tạo từ FXML");
            return;
        }

        showSingleGraph();

        app_parent.setTop(menuPane);
    }

    public void showSingleGraph() {
        if (xTimeChart == null || yTimeChart == null || vxTimeChart == null ||
            vyTimeChart == null || trajectoryChart == null) {
            return;
        }

        xTimeChart.setVisible(false);
        yTimeChart.setVisible(false);
        vxTimeChart.setVisible(false);
        vyTimeChart.setVisible(false);

        trajectoryChart.setVisible(true);
    }

    public void showFourGraphs() {
        if (xTimeChart == null || yTimeChart == null || vxTimeChart == null ||
            vyTimeChart == null || trajectoryChart == null) {
            return;
        }

        trajectoryChart.setVisible(false);

        xTimeChart.setVisible(true);
        yTimeChart.setVisible(true);
        vxTimeChart.setVisible(true);
        vyTimeChart.setVisible(true);
    }

    public void calculateTrajectory() {
        if (menuController == null) return;

        double[] values = menuController.getSpinnerValues();
        double alpha = values[0];
        double mass = values[1];
        int nSteps = (int) values[2];
        double x0 = values[3];
        double y0 = values[4];
        double vx0 = values[5];
        double vy0 = values[6];

        TrajectoryModel.TrajectoryData data = TrajectoryModel.calculateTrajectory(
                alpha, mass, nSteps, x0, y0, vx0, vy0);

        updateCharts(data);
    }

    private void updateCharts(TrajectoryModel.TrajectoryData data) {
        trajectoryChart.getData().clear();
        xTimeChart.getData().clear();
        yTimeChart.getData().clear();
        vxTimeChart.getData().clear();
        vyTimeChart.getData().clear();

        XYChart.Series<Number, Number> trajectorySeries = new XYChart.Series<>();
        trajectorySeries.setName("Quỹ đạo");

        XYChart.Series<Number, Number> xSeries = new XYChart.Series<>();
        xSeries.setName("x(t)");

        XYChart.Series<Number, Number> ySeries = new XYChart.Series<>();
        ySeries.setName("y(t)");

        XYChart.Series<Number, Number> vxSeries = new XYChart.Series<>();
        vxSeries.setName("vx(t)");

        XYChart.Series<Number, Number> vySeries = new XYChart.Series<>();
        vySeries.setName("vy(t)");

        for (int i = 0; i < data.time.length; i++) {
            if (data.y[i] >= 0) {
                XYChart.Data<Number, Number> trajectoryPoint = new XYChart.Data<>(data.x[i], data.y[i]);
                trajectorySeries.getData().add(trajectoryPoint);

                XYChart.Data<Number, Number> xPoint = new XYChart.Data<>(data.time[i], data.x[i]);
                xSeries.getData().add(xPoint);

                XYChart.Data<Number, Number> yPoint = new XYChart.Data<>(data.time[i], data.y[i]);
                ySeries.getData().add(yPoint);

                XYChart.Data<Number, Number> vxPoint = new XYChart.Data<>(data.time[i], data.vx[i]);
                vxSeries.getData().add(vxPoint);

                XYChart.Data<Number, Number> vyPoint = new XYChart.Data<>(data.time[i], data.vy[i]);
                vySeries.getData().add(vyPoint);
            }
        }

        trajectoryChart.getData().add(trajectorySeries);
        xTimeChart.getData().add(xSeries);
        yTimeChart.getData().add(ySeries);
        vxTimeChart.getData().add(vxSeries);
        vyTimeChart.getData().add(vySeries);

        addTooltips(trajectorySeries, data);
        addTooltips(xSeries, data);
        addTooltips(ySeries, data);
        addTooltips(vxSeries, data);
        addTooltips(vySeries, data);
    }

    private void addTooltips(XYChart.Series<Number, Number> series, TrajectoryModel.TrajectoryData data) {
        int dataSize = series.getData().size();

        for (int i = 0; i < dataSize; i++) {
            final int index = i;
            XYChart.Data<Number, Number> dataPoint = series.getData().get(i);

            Tooltip tooltip = new Tooltip(
                    String.format("Time: %.2f s\nX: %.2f m\nY: %.2f m\nVx: %.2f m/s\nVy: %.2f m/s",
                            data.time[index], data.x[index], data.y[index],
                            data.vx[index], data.vy[index])
            );

            tooltip.setShowDelay(Duration.millis(100));
            tooltip.setHideDelay(Duration.millis(200));

            Node node = dataPoint.getNode();

            if (node != null) {
                Tooltip.install(node, tooltip);
            } else {
                Platform.runLater(() -> {
                    Node newNode = dataPoint.getNode();
                    if (newNode != null) {
                        Tooltip.install(newNode, tooltip);
                    }
                });
            }
        }
    }
}
