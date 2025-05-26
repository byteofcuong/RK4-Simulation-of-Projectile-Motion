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

        TrajectoryData result = calculateWithEstimatedTime(alpha, mass, nSteps, x0, y0, vx0, vy0, estimatedTime);

        if (result.y[result.y.length-1] > 0 && result.vy[result.vy.length-1] < 0) {
            return calculateWithEstimatedTime(alpha, mass, nSteps, x0, y0, vx0, vy0, estimatedTime * 3);
        }

        return result;
    }

    private static TrajectoryData calculateWithEstimatedTime(
            double alpha, double mass, int nSteps,
            double x0, double y0, double vx0, double vy0, double estimatedTime) {

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

        int lastValidIndex = nSteps;
        boolean foundGroundContact = false;
        for (int i = 0; i < nSteps + 1; i++) {
            if (y[i] < 0) {
                lastValidIndex = i;
                foundGroundContact = true;
                break;
            }
        }

        if (foundGroundContact) {
            int i0 = lastValidIndex - 1;
            int i1 = lastValidIndex;

            double t = (0 - y[i0]) / (y[i1] - y[i0]);

            if (Double.isNaN(t) || Double.isInfinite(t) || Math.abs(y[i1] - y[i0]) < 1e-10) {
                t = 0.5;
            }

            double interpolatedTime = time[i0] + t * (time[i1] - time[i0]);
            double interpolatedX = x[i0] + t * (x[i1] - x[i0]);
            double interpolatedVx = vx[i0] + t * (vx[i1] - vx[i0]);
            double interpolatedVy = vy[i0] + t * (vy[i1] - vy[i0]);

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

    public static TrajectoryData calculateTrajectoryEuler(
            double alpha, double mass, int nSteps,
            double x0, double y0, double vx0, double vy0) {

        double estimatedTime = 2 * vy0 / G;
        if (estimatedTime <= 0) {
            estimatedTime = 10;
        }

        TrajectoryData result = calculateWithEstimatedTimeEuler(alpha, mass, nSteps, x0, y0, vx0, vy0, estimatedTime);

        if (result.y[result.y.length-1] > 0 && result.vy[result.vy.length-1] < 0) {
            return calculateWithEstimatedTimeEuler(alpha, mass, nSteps, x0, y0, vx0, vy0, estimatedTime * 3);
        }

        return result;
    }

    private static TrajectoryData calculateWithEstimatedTimeEuler(
            double alpha, double mass, int nSteps,
            double x0, double y0, double vx0, double vy0, double estimatedTime) {

        Euler.TriFunction<Double, Double, Double, Double> horizontalAcceleration =
                (t, x, vx) -> -alpha * vx / mass;

        Euler.TriFunction<Double, Double, Double, Double> verticalAcceleration =
                (t, y, vy) -> -G - alpha * vy / mass;

        double[][] horizontalResults = Euler.secondOrderEuler(
                horizontalAcceleration, 0, estimatedTime, x0, vx0, nSteps);

        double[][] verticalResults = Euler.secondOrderEuler(
                verticalAcceleration, 0, estimatedTime, y0, vy0, nSteps);

        double[] x = horizontalResults[0];
        double[] vx = horizontalResults[1];
        double[] y = verticalResults[0];
        double[] vy = verticalResults[1];

        double[] time = Euler.linspace(0, estimatedTime, nSteps + 1);

        int lastValidIndex = nSteps;
        boolean foundGroundContact = false;
        for (int i = 0; i < nSteps + 1; i++) {
            if (y[i] < 0) {
                lastValidIndex = i;
                foundGroundContact = true;
                break;
            }
        }

        if (foundGroundContact) {
            int i0 = lastValidIndex - 1;
            int i1 = lastValidIndex;
            double t = (0 - y[i0]) / (y[i1] - y[i0]);
            if (Double.isNaN(t) || Double.isInfinite(t) || Math.abs(y[i1] - y[i0]) < 1e-10) {
                t = 0.5;
            }
            double interpolatedTime = time[i0] + t * (time[i1] - time[i0]);
            double interpolatedX = x[i0] + t * (x[i1] - x[i0]);
            double interpolatedVx = vx[i0] + t * (vx[i1] - vx[i0]);
            double interpolatedVy = vy[i0] + t * (vy[i1] - vy[i0]);

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