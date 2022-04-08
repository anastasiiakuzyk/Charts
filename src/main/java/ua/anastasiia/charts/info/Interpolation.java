package ua.anastasiia.charts.info;

import org.apache.commons.math3.analysis.interpolation.DividedDifferenceInterpolator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionNewtonForm;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Interpolation {
    private static DataSet set;
    private static Method method;

    public static void setMethod(Method method) {
        Interpolation.method = method;
    }

    private static void Newton() {
        DataSet indexesSet = getSetOfIndexes();
        double[] value = new double[indexesSet.getInX().length];

        for (int i = 0; i < indexesSet.getInX().length; i++) {
            DataSet point = interpolateOnePoint(indexesSet.getX(), indexesSet.getY(), indexesSet.getInX()[i]);
            PolynomialFunctionNewtonForm interpolate = new DividedDifferenceInterpolator().interpolate(point.getX(), point.getY());
            value[i] = interpolate.value(indexesSet.getInX()[i]);
        }
        set.setInY(value);
    }

    private static void Lagrange() {
        DataSet indexesSet = getSetOfIndexes();
        double[] value = new double[indexesSet.getInX().length];

        for (int i = 0; i < indexesSet.getInX().length; i++) {
            DataSet point = interpolateOnePoint(indexesSet.getX(), indexesSet.getY(), indexesSet.getInX()[i]);
            value[i] = PolynomialFunctionLagrangeForm.evaluate(point.getX(), point.getY(), indexesSet.getInX()[i]);
        }
        set.setInY(value);
    }

    private static DataSet getSetOfIndexes() {
        double[] inX = set.getInX();
        double[] x = set.getX();
        double[] y = set.getY();
        List<Double> xGlobalList = Arrays.stream(x).boxed().collect(Collectors.toList());
        for (double v : inX) {
            xGlobalList.add(v);
        }
        List<Double> xGlobalListWithInX = xGlobalList.stream().sorted().collect(Collectors.toList());
        List<Double> xIndexesList = new ArrayList<>();
        List<Double> inXListIndexes = new ArrayList<>();
        for (int i = 0; i < xGlobalListWithInX.size(); i++) {
            for (double v : inX) {
                if (xGlobalListWithInX.get(i).equals(v)) {
                    inXListIndexes.add((double) i + 1);
                }
            }
            xIndexesList.add((double) i + 1);
        }

        for (int i = 0; i < inX.length; i++) {
            xIndexesList.remove(inXListIndexes.get(i));
        }
        double[] xIndexes = Arrays.stream(xIndexesList.stream().mapToDouble(Double::doubleValue).toArray()).toArray();
        double[] inXIndexes = Arrays.stream(inXListIndexes.stream().mapToDouble(Double::doubleValue).toArray()).toArray();
        return new DataSet(xIndexes, y, inXIndexes);
    }

    private static DataSet interpolateOnePoint(double[] xGlobal, double[] yGlobal, double inX) {
        List<Double> xGlobalList = Arrays.stream(xGlobal).boxed().collect(Collectors.toList());
        List<Double> yGlobalList = Arrays.stream(yGlobal).boxed().collect(Collectors.toList());
        int size = xGlobalList.size();
        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 1; i < size; i++) {
            if (xGlobalList.get(i - 1) < inX && xGlobalList.get(i) > inX) {
                for (int j = 0; j < 5; j++) {
                    if (i - 1 >= j) {
                        fromIndex = i - 1 - j;
                    }
                    if (i + j + 1 <= size) {
                        toIndex = i + 1 + j;
                    }
                }
            }
        }

        List<Double> xList = xGlobalList.subList(fromIndex, toIndex);
        List<Double> yList = yGlobalList.subList(fromIndex, toIndex);
        double[] x = Arrays.stream(xList.stream().mapToDouble(Double::doubleValue).toArray()).toArray();
        double[] y = Arrays.stream(yList.stream().mapToDouble(Double::doubleValue).toArray()).toArray();

        return new DataSet(x, y);
    }

    private static void Spline() {
        PolynomialSplineFunction interpolate = new SplineInterpolator().interpolate(set.getX(), set.getY());
        double[] inX = set.getInX();
        double[] value = new double[inX.length];
        for (int i = 0; i < inX.length; i++) {
            value[i] = interpolate.value(inX[i]);
        }
        set.setInY(value);
    }

    private static void SmallSquares() {
        SimpleRegression simpleRegression = new SimpleRegression();
        for (int i = 0; i < set.getY().length; i++) {
            simpleRegression.addData(set.getX()[i], set.getY()[i]);
        }
        double[] inX = set.getInX();
        double[] value = new double[inX.length];
        for (int i = 0; i < inX.length; i++) {
            value[i] = simpleRegression.predict(inX[i]);
        }
        set.setInY(value);
    }

    private static void Linear() {
        LinearInterpolator interpolator = new LinearInterpolator();
        PolynomialSplineFunction interpolate = interpolator.interpolate(set.getX(), set.getY());
        double[] inX = set.getInX();
        double[] value = new double[inX.length];
        for (int i = 0; i < inX.length; i++) {
            value[i] = interpolate.value(inX[i]);
        }
        set.setInY(value);
    }

    enum Method {
        Newton, Lagrange, Spline, SmallSquares, Linear
    }

    public static DataSet interpolate(double[] x, double[] y, double[] inX) {
        set = new DataSet(x, y, inX);
        switch (method) {
            case Newton:
                Newton();
                break;
            case Lagrange:
                Lagrange();
                break;
            case Spline:
                Spline();
                break;
            case SmallSquares:
                SmallSquares();
                break;
            case Linear:
                Linear();
                break;
            default:
                System.exit(-1);
        }
        return set;
    }
}
