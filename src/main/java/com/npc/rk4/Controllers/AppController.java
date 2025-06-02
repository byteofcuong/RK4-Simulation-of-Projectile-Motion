package com.npc.rk4.Controllers;

import com.npc.rk4.Models.*;
import com.npc.rk4.Views.ViewFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Point2D dragStart;
    private Map<LineChart<Number, Number>, double[]> originalRanges = new HashMap<>();

    private static final List<ODESolver> solvers = Arrays.asList(
            new EulerSolver(),
            new RungeKuttaSolver(),
            new HeunSolver(),
            new MidpointEulerSolver());
    private static final List<String> solverNames = Arrays.asList(
            "Euler", "Runge-Kutta", "Heun", "Midpoint Euler");

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
        trajectoryChart.setAnimated(false);
        xTimeChart.setAnimated(false);
        yTimeChart.setAnimated(false);
        vxTimeChart.setAnimated(false);
        vyTimeChart.setAnimated(false);

        setupZoomAndPan(trajectoryChart);
        setupZoomAndPan(xTimeChart);
        setupZoomAndPan(yTimeChart);
        setupZoomAndPan(vxTimeChart);
        setupZoomAndPan(vyTimeChart);
    }

    private void setupZoomAndPan(LineChart<Number, Number> chart) {
        chart.setOnScroll((ScrollEvent event) -> {
            event.consume();
            double zoomFactor = event.getDeltaY() > 0 ? 0.9 : 1.1;
            zoom(chart, zoomFactor);
        });

        chart.setOnMousePressed((MouseEvent event) -> {
            dragStart = new Point2D(event.getX(), event.getY());
        });

        chart.setOnMouseDragged((MouseEvent event) -> {
            if (dragStart == null)
                return;
            double dragX = event.getX() - dragStart.getX();
            double dragY = event.getY() - dragStart.getY();
            pan(chart, dragX, dragY);
            dragStart = new Point2D(event.getX(), event.getY());
        });

        chart.setOnMouseReleased((MouseEvent event) -> {
            dragStart = null;
        });
    }

    private void zoom(LineChart<Number, Number> chart, double factor) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        double xLower = xAxis.getLowerBound();
        double xUpper = xAxis.getUpperBound();
        double yLower = yAxis.getLowerBound();
        double yUpper = yAxis.getUpperBound();

        double xRange = xUpper - xLower;
        double yRange = yUpper - yLower;
        double xMid = (xLower + xUpper) / 2;
        double yMid = (yLower + yUpper) / 2;

        xAxis.setLowerBound(xMid - (xRange * factor) / 2);
        xAxis.setUpperBound(xMid + (xRange * factor) / 2);
        yAxis.setLowerBound(yMid - (yRange * factor) / 2);
        yAxis.setUpperBound(yMid + (yRange * factor) / 2);
    }

    private void pan(LineChart<Number, Number> chart, double deltaX, double deltaY) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        double xLower = xAxis.getLowerBound();
        double xUpper = xAxis.getUpperBound();
        double yLower = yAxis.getLowerBound();
        double yUpper = yAxis.getUpperBound();

        double xRange = xUpper - xLower;
        double yRange = yUpper - yLower;

        double xDelta = (deltaX / chart.getWidth()) * xRange;
        double yDelta = (deltaY / chart.getHeight()) * yRange;

        xAxis.setLowerBound(xLower - xDelta);
        xAxis.setUpperBound(xUpper - xDelta);
        yAxis.setLowerBound(yLower + yDelta);
        yAxis.setUpperBound(yUpper + yDelta);
    }

    public void showSingleGraph() {
        if (!chartsInitialized)
            return;

        trajectoryChart.setVisible(true);
        xTimeChart.setVisible(false);
        yTimeChart.setVisible(false);
        vxTimeChart.setVisible(false);
        vyTimeChart.setVisible(false);
    }

    public void showFourGraphs() {
        if (!chartsInitialized)
            return;

        trajectoryChart.setVisible(false);
        xTimeChart.setVisible(true);
        yTimeChart.setVisible(true);
        vxTimeChart.setVisible(true);
        vyTimeChart.setVisible(true);

        xTimeChart.setLegendVisible(false);
        yTimeChart.setLegendVisible(false);
        vxTimeChart.setLegendVisible(false);
        vyTimeChart.setLegendVisible(false);
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

            // Tính toán cho tất cả solver
            List<TrajectoryModel.TrajectoryData> allData = new ArrayList<>();
            for (ODESolver solver : solvers) {
                allData.add(TrajectoryModel.calculateTrajectory(
                        alpha, mass, nSteps, x0, y0, vx0, vy0, solver));
            }
            updateCharts(allData);
        } catch (Exception e) {
            System.err.println("Lỗi khi tính toán quỹ đạo: " + e.getMessage());
        }
    }

    private void updateCharts(List<TrajectoryModel.TrajectoryData> allData) {
        if (allData == null || allData.isEmpty() || !chartsInitialized)
            return;

        trajectoryChart.getData().clear();
        xTimeChart.getData().clear();
        yTimeChart.getData().clear();
        vxTimeChart.getData().clear();
        vyTimeChart.getData().clear();

        for (int idx = 0; idx < allData.size(); idx++) {
            TrajectoryModel.TrajectoryData data = allData.get(idx);
            String name = solverNames.get(idx);

            XYChart.Series<Number, Number> trajectorySeries = createSeries(data.x, data.y, "Quỹ đạo " + name);
            XYChart.Series<Number, Number> xSeries = createSeries(data.time, data.x, "x(t) " + name);
            XYChart.Series<Number, Number> ySeries = createSeries(data.time, data.y, "y(t) " + name);
            XYChart.Series<Number, Number> vxSeries = createSeries(data.time, data.vx, "vx(t) " + name);
            XYChart.Series<Number, Number> vySeries = createSeries(data.time, data.vy, "vy(t) " + name);

            trajectoryChart.getData().add(trajectorySeries);
            xTimeChart.getData().add(xSeries);
            yTimeChart.getData().add(ySeries);
            vxTimeChart.getData().add(vxSeries);
            vyTimeChart.getData().add(vySeries);

            Platform.runLater(() -> {
                addTooltips(trajectorySeries, data);
                addTooltips(xSeries, data);
                addTooltips(ySeries, data);
                addTooltips(vxSeries, data);
                addTooltips(vySeries, data);
            });
        }

        Platform.runLater(() -> {
            saveChartRanges(trajectoryChart);
            saveChartRanges(xTimeChart);
            saveChartRanges(yTimeChart);
            saveChartRanges(vxTimeChart);
            saveChartRanges(vyTimeChart);
        });
    }

    private XYChart.Series<Number, Number> createSeries(double[] xData, double[] yData, String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);

        boolean isVyGraph = name.contains("vy(t)");

        for (int i = 0; i < xData.length; i++) {
            if (isVyGraph || yData[i] >= 0) {
                series.getData().add(new XYChart.Data<>(xData[i], yData[i]));
            }
        }

        return series;
    }

    private void saveChartRanges(LineChart<Number, Number> chart) {
        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

        originalRanges.put(chart, new double[] {
                xAxis.getLowerBound(), xAxis.getUpperBound(),
                yAxis.getLowerBound(), yAxis.getUpperBound()
        });
    }

    private void addTooltips(XYChart.Series<Number, Number> series, TrajectoryModel.TrajectoryData data) {
        int dataSize = series.getData().size();
        int x;
        if (series.getName().charAt(0) == 'Q') {
            x = 8;
        } else {
            x = series.getName().indexOf(' ') + 1;
        }
        String seriesName = series.getName().substring(x);

        for (int i = 0; i < dataSize; i++) {
            final int index = i;
            XYChart.Data<Number, Number> dataPoint = series.getData().get(i);
            Node node = dataPoint.getNode();

            if (node != null) {
                String tooltipText = String.format(
                        "%s\nTime: %.2f s\nX: %.2f m\nY: %.2f m\nVx: %.2f m/s\nVy: %.2f m/s",
                        seriesName,
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
