package ua.anastasiia.charts.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Wind extends ErrorsFixer {

    public Wind(List<Columns> infoList) {
        super(infoList);
    }

    public List<Columns> getErrors() {
        ArrayList<Columns> errors = new ArrayList<>();
        for (Columns row : infoList) {
            if (row.getWindDirection() == null) {
                errors.add(row);
            }
        }
        return errors;
    }

    protected double[] getY(List<Columns> list) {
        double[] y = new double[list.size()];
        for (int i = 0; i < y.length; i++) {
            y[i] = directionToIndex(list.get(i));
        }
        return y;
    }

    protected void changeToInterpolated(DataSet set, Columns row, int j) {
        row.setWindDirection(indexToDirection(set.getInY()[j]));
    }


    private static int directionToIndex(Columns info) {
        String[] WindDirEng = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "UNSTEADY", "CALM"};
        int result = -1;
        for (int j = 0; j < WindDirEng.length; j++) {
            if (Objects.equals(info.getWindDirection(), WindDirEng[j])) {
                result = j;
            }
        }
        return result;
    }

    private static String indexToDirection(double index) {
        String[] WindDirEng = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "UNSTEADY"};
        String result = "";
        for (int j = 0; j < WindDirEng.length; j++) {
            if (Math.abs(Math.round(index)) == j) {
                result = WindDirEng[j];
            }
        }
        if (result.equals("")) {
            result = WindDirEng[WindDirEng.length - 1];
        }
        return result;
    }

}
