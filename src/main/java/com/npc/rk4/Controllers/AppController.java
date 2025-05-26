package com.npc.rk4.Controllers;

import com.npc.rk4.Models.TrajectoryModel;
import com.npc.rk4.Models.Euler;
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
    private boolean chartsInitialized = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (trajectoryChart == null || xTimeChart == null || yTimeChart == null ||
            vxTimeChart == null || vyTimeChart == null) {
            System.err.println("Một số biểu đồ không được khởi tạo từ FXML");
            return;
        }
        
        chartsInitialized = true;

        configureCharts();

        showSingleGraph();

        Pane menuPane = ViewFactory.getInstance().getMenu();
        menuController = ViewFactory.getInstance().getMenuController();

        if (menuController != null) {
            menuController.setAppController(this);
        }
        
        app_parent.setTop(menuPane);
    }

    private void configureCharts() {
        trajectoryChart.setAnimated(true);

        xTimeChart.setAnimated(true);

        yTimeChart.setAnimated(true);

        vxTimeChart.setAnimated(true);

        vyTimeChart.setAnimated(true);
    }

    public void showSingleGraph() {
        if (!chartsInitialized) return;

        trajectoryChart.setVisible(true);
        xTimeChart.setVisible(false);
        yTimeChart.setVisible(false);
        vxTimeChart.setVisible(false);
        vyTimeChart.setVisible(false);
    }

    public void showFourGraphs() {
        if (!chartsInitialized) return;

        trajectoryChart.setVisible(false);
        xTimeChart.setVisible(true);
        yTimeChart.setVisible(true);
        vxTimeChart.setVisible(true);
        vyTimeChart.setVisible(true);
    }

    public void calculateTrajectory() {
        if (menuController == null) {
            System.err.println("MenuController chưa được khởi tạo");
            return;
        }
        
        if (!chartsInitialized) {
            System.err.println("Các đồ thị chưa được khởi tạo");
            return;
        }

        try {
            double[] values = menuController.getSpinnerValues();
            double alpha = values[0];
            double mass = values[1];
            int nSteps = (int) values[2];
            double x0 = values[3];
            double y0 = values[4];
            double vx0 = values[5];
            double vy0 = values[6];

            TrajectoryModel.TrajectoryData rk4Data = TrajectoryModel.calculateTrajectory(
                    alpha, mass, nSteps, x0, y0, vx0, vy0);
            TrajectoryModel.TrajectoryData eulerData = TrajectoryModel.calculateTrajectoryEuler(
                    alpha, mass, nSteps, x0, y0, vx0, vy0);

            updateCharts(rk4Data, eulerData);
        } catch (Exception e) {
            System.err.println("Lỗi khi tính toán quỹ đạo: " + e.getMessage());
        }
    }

    private void updateCharts(TrajectoryModel.TrajectoryData rk4Data, TrajectoryModel.TrajectoryData eulerData) {
        if (rk4Data == null || !chartsInitialized) return;

        trajectoryChart.getData().clear();
        xTimeChart.getData().clear();
        yTimeChart.getData().clear();
        vxTimeChart.getData().clear();
        vyTimeChart.getData().clear();

        XYChart.Series<Number, Number> rk4TrajectorySeries = new XYChart.Series<>();
        rk4TrajectorySeries.setName("Quỹ đạo RK4");
        XYChart.Series<Number, Number> rk4XSeries = new XYChart.Series<>();
        rk4XSeries.setName("x(t) RK4");
        XYChart.Series<Number, Number> rk4YSeries = new XYChart.Series<>();
        rk4YSeries.setName("y(t) RK4");
        XYChart.Series<Number, Number> rk4VxSeries = new XYChart.Series<>();
        rk4VxSeries.setName("vx(t) RK4");
        XYChart.Series<Number, Number> rk4VySeries = new XYChart.Series<>();
        rk4VySeries.setName("vy(t) RK4");

        XYChart.Series<Number, Number> eulerTrajectorySeries = new XYChart.Series<>();
        eulerTrajectorySeries.setName("Quỹ đạo Euler");
        XYChart.Series<Number, Number> eulerXSeries = new XYChart.Series<>();
        eulerXSeries.setName("x(t) Euler");
        XYChart.Series<Number, Number> eulerYSeries = new XYChart.Series<>();
        eulerYSeries.setName("y(t) Euler");
        XYChart.Series<Number, Number> eulerVxSeries = new XYChart.Series<>();
        eulerVxSeries.setName("vx(t) Euler");
        XYChart.Series<Number, Number> eulerVySeries = new XYChart.Series<>();
        eulerVySeries.setName("vy(t) Euler");

        for (int i = 0; i < rk4Data.time.length; i++) {
            if (rk4Data.y[i] >= 0) {
                rk4TrajectorySeries.getData().add(new XYChart.Data<>(rk4Data.x[i], rk4Data.y[i]));
                rk4XSeries.getData().add(new XYChart.Data<>(rk4Data.time[i], rk4Data.x[i]));
                rk4YSeries.getData().add(new XYChart.Data<>(rk4Data.time[i], rk4Data.y[i]));
                rk4VxSeries.getData().add(new XYChart.Data<>(rk4Data.time[i], rk4Data.vx[i]));
                rk4VySeries.getData().add(new XYChart.Data<>(rk4Data.time[i], rk4Data.vy[i]));
            }
        }
        for (int i = 0; i < eulerData.time.length; i++) {
            if (eulerData.y[i] >= 0) {
                eulerTrajectorySeries.getData().add(new XYChart.Data<>(eulerData.x[i], eulerData.y[i]));
                eulerXSeries.getData().add(new XYChart.Data<>(eulerData.time[i], eulerData.x[i]));
                eulerYSeries.getData().add(new XYChart.Data<>(eulerData.time[i], eulerData.y[i]));
                eulerVxSeries.getData().add(new XYChart.Data<>(eulerData.time[i], eulerData.vx[i]));
                eulerVySeries.getData().add(new XYChart.Data<>(eulerData.time[i], eulerData.vy[i]));
            }
        }

        trajectoryChart.getData().addAll(rk4TrajectorySeries, eulerTrajectorySeries);
        xTimeChart.getData().addAll(rk4XSeries, eulerXSeries);
        yTimeChart.getData().addAll(rk4YSeries, eulerYSeries);
        vxTimeChart.getData().addAll(rk4VxSeries, eulerVxSeries);
        vyTimeChart.getData().addAll(rk4VySeries, eulerVySeries);

        Platform.runLater(() -> {
            addTooltips(rk4TrajectorySeries, rk4Data);
            addTooltips(eulerTrajectorySeries, eulerData);
            addTooltips(rk4XSeries, rk4Data);
            addTooltips(eulerXSeries, eulerData);
            addTooltips(rk4YSeries, rk4Data);
            addTooltips(eulerYSeries, eulerData);
            addTooltips(rk4VxSeries, rk4Data);
            addTooltips(eulerVxSeries, eulerData);
            addTooltips(rk4VySeries, rk4Data);
            addTooltips(eulerVySeries, eulerData);
        });
    }

    private void addTooltips(XYChart.Series<Number, Number> series, TrajectoryModel.TrajectoryData data) {
        int dataSize = series.getData().size();
        
        for (int i = 0; i < dataSize; i++) {
            final int index = i;
            XYChart.Data<Number, Number> dataPoint = series.getData().get(i);
            Node node = dataPoint.getNode();
            
            if (node != null) {
                String tooltipText = String.format(
                        "Time: %.2f s\nX: %.2f m\nY: %.2f m\nVx: %.2f m/s\nVy: %.2f m/s",
                        data.time[index], data.x[index], data.y[index],
                        data.vx[index], data.vy[index]);
                
                Tooltip tooltip = new Tooltip(tooltipText);
                tooltip.setShowDelay(Duration.millis(100));
                tooltip.setHideDelay(Duration.millis(200));
                
                Tooltip.install(node, tooltip);
            }
        }
    }
}
