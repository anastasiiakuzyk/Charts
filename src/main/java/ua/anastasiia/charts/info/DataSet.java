package ua.anastasiia.charts.info;

import java.util.Arrays;

public class DataSet {
    double[] x;
    double[] y;
    double[] inX;
    double[] inY;

    public DataSet(double[] x, double[] y, double[] inX) {
        this.x = x;
        this.y = y;
        this.inX = inX;
    }

    public DataSet(double[] x, double[] y) {
        this.x = x;
        this.y = y;
    }

    public void setInX(double[] inX) {
        this.inX = inX;
    }

    public double[] getX() {
        return x;
    }

    public double[] getY() {
        return y;
    }

    public double[] getInX() {
        return inX;
    }

    public double[] getInY() {
        return inY;
    }

    public void setInY(double[] inY) {
        this.inY = inY;
    }

    @Override
    public String toString() {
        return "info.DataSet{" +
                "x=" + Arrays.toString(x) +
                ", y=" + Arrays.toString(y) +
                ", inX=" + Arrays.toString(inX) +
                ", inY=" + Arrays.toString(inY) +
                '}';
    }
}
