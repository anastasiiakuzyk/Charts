package ua.anastasiia.charts.info;

import java.util.ArrayList;
import java.util.List;

public class WindSpeed extends ErrorsFixer {

    public WindSpeed(List<Columns> infoList) {
        super(infoList);
    }

    public List<Columns> getErrors() {
        ArrayList<Columns> errors = new ArrayList<>();
        for (Columns row : infoList) {
            if (row.getWindSpeed() < 0) {
                errors.add(row);
            }
        }
        return errors;
    }

    public void edit(List<Columns> errors) {
        for (int i = 1; i < infoList.size(); i++) {
            if (infoList.get(i).getWindSpeed() < 0) {
                infoList.get(i).setWindSpeed(Math.abs(infoList.get(i).getWindSpeed()));
            }
        }
        completedList = infoList;
    }

    @Override
    protected double[] getY(List<Columns> list) {
        return null;
    }

    @Override
    protected void changeToInterpolated(DataSet set, Columns row, int j) {
    }

}
