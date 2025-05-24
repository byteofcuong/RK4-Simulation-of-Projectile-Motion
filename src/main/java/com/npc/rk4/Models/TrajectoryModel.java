package com.npc.rk4.Models;

public class TrajectoryModel {
    private static final double G = 9.81;

    public static class TrajectoryData {
        public double[] time;
        public double[] x;
        public double[] y;
        public double[] vx;
        public double[] vy;

        public TrajectoryData(double[] time, double[] x, double[] y, double[] vx, double[] vy) {
            this.time = time;
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }
    }

    public static TrajectoryData calculateTrajectory(
            double alpha, double mass, int nSteps,
            double x0, double y0, double vx0, double vy0) {

        double estimatedTime = 2 * vy0 / G;
        if (estimatedTime <= 0) {
            estimatedTime = 10;
        }

        RungeKutta.TriFunction<Double, Double, Double, Double> horizontalAcceleration =
                (t, x, vx) -> -alpha * vx / mass;

        RungeKutta.TriFunction<Double, Double, Double, Double> verticalAcceleration =
                (t, y, vy) -> -G - alpha * vy / mass;

        double[][] horizontalResults = RungeKutta.secondOrderRungeKutta(
                horizontalAcceleration, 0, estimatedTime, x0, vx0, nSteps);

        double[][] verticalResults = RungeKutta.secondOrderRungeKutta(
                verticalAcceleration, 0, estimatedTime, y0, vy0, nSteps);

        double[] x = horizontalResults[0];
        double[] vx = horizontalResults[1];
        double[] y = verticalResults[0];
        double[] vy = verticalResults[1];

        double[] time = RungeKutta.linspace(0, estimatedTime, nSteps + 1);

        return new TrajectoryData(time, x, y, vx, vy);
    }
}