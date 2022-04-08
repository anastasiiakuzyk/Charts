package ua.anastasiia.charts.info;

import java.util.ArrayList;
import java.util.List;

public class Temperature extends ErrorsFixer {

    public Temperature(List<Columns> infoList) {
        super(infoList);
    }

    public List<Columns> getErrors() {
        ArrayList<Columns> errors = new ArrayList<>();
        for (Columns row : infoList) {
            if (row.getTemperature() == null || row.getTemperature() > 40 || row.getTemperature() < -40) {
                errors.add(row);
            }
        }
        return errors;
    }

    protected double[] getY(List<Columns> list) {
        double[] y = new double[list.size()];
        for (int i = 0; i < y.length; i++) {
            y[i] = list.get(i).getTemperature();
        }
        return y;
    }

    protected void changeToInterpolated(DataSet set, Columns row, int j) {
        row.setTemperature(set.getInY()[j]);
    }

}
