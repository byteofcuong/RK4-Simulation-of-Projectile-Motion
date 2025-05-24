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

        // Tìm điểm mà quỹ đạo chạm đất
        int lastValidIndex = nSteps;
        for (int i = 0; i < nSteps; i++) {
            if (y[i] < 0) {
                lastValidIndex = i;
                break;
            }
        }

        // nội suy Lagrange xác định thời điểm và vị trí chạm đất
        if (lastValidIndex < nSteps) {
            int i0 = lastValidIndex - 1;
            int i1 = lastValidIndex;

            double y0Value = 0;

            double L0 = (y0Value - y[i1]) / (y[i0] - y[i1]);
            double L1 = (y0Value - y[i0]) / (y[i1] - y[i0]);

            double interpolatedTime = time[i0] * L0 + time[i1] * L1;
            double interpolatedX = x[i0] * L0 + x[i1] * L1;
            double interpolatedVx = vx[i0] * L0 + vx[i1] * L1;
            double interpolatedVy = vy[i0] * L0 + vy[i1] * L1;

            time[i1] = interpolatedTime;
            x[i1] = interpolatedX;
            y[i1] = 0;
            vx[i1] = interpolatedVx;
            vy[i1] = interpolatedVy;

            double[] trimmedTime = new double[i1 + 1];
            double[] trimmedX = new double[i1 + 1];
            double[] trimmedY = new double[i1 + 1];
            double[] trimmedVx = new double[i1 + 1];
            double[] trimmedVy = new double[i1 + 1];

            System.arraycopy(time, 0, trimmedTime, 0, i1 + 1);
            System.arraycopy(x, 0, trimmedX, 0, i1 + 1);
            System.arraycopy(y, 0, trimmedY, 0, i1 + 1);
            System.arraycopy(vx, 0, trimmedVx, 0, i1 + 1);
            System.arraycopy(vy, 0, trimmedVy, 0, i1 + 1);

            return new TrajectoryData(trimmedTime, trimmedX, trimmedY, trimmedVx, trimmedVy);
        }


        return new TrajectoryData(time, x, y, vx, vy);
    }
}