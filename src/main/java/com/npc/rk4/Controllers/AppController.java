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

            TrajectoryModel.TrajectoryData data = TrajectoryModel.calculateTrajectory(
                    alpha, mass, nSteps, x0, y0, vx0, vy0);

            updateCharts(data);
        } catch (Exception e) {
            System.err.println("Lỗi khi tính toán quỹ đạo: " + e.getMessage());
        }
    }

    private void updateCharts(TrajectoryModel.TrajectoryData data) {
        if (data == null || !chartsInitialized) return;

        trajectoryChart.getData().clear();
        xTimeChart.getData().clear();
        yTimeChart.getData().clear();
        vxTimeChart.getData().clear();
        vyTimeChart.getData().clear();

        // tạo các series mới
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

        // thêm dữ liệu vào các series
        for (int i = 0; i < data.time.length; i++) {
            if (data.y[i] >= 0) {  // chỉ hiển thị dữ liệu khi y >= 0
                trajectorySeries.getData().add(new XYChart.Data<>(data.x[i], data.y[i]));
                xSeries.getData().add(new XYChart.Data<>(data.time[i], data.x[i]));
                ySeries.getData().add(new XYChart.Data<>(data.time[i], data.y[i]));
                vxSeries.getData().add(new XYChart.Data<>(data.time[i], data.vx[i]));
                vySeries.getData().add(new XYChart.Data<>(data.time[i], data.vy[i]));
            }
        }

        // thêm series vào các đồ thị
        trajectoryChart.getData().add(trajectorySeries);
        xTimeChart.getData().add(xSeries);
        yTimeChart.getData().add(ySeries);
        vxTimeChart.getData().add(vxSeries);
        vyTimeChart.getData().add(vySeries);

        // thêm tooltips
        Platform.runLater(() -> {
            addTooltips(trajectorySeries, data);
            addTooltips(xSeries, data);
            addTooltips(ySeries, data);
            addTooltips(vxSeries, data);
            addTooltips(vySeries, data);
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
