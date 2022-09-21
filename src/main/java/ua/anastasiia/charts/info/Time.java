package ua.anastasiia.charts.info;

import java.util.ArrayList;
import java.util.List;

public class Time extends ErrorsFixer {

    public Time(List<Columns> infoList) {
        super(infoList);
    }

    public List<Columns> getErrors() {
        ArrayList<Columns> errors = new ArrayList<>();
        for (int i = 1; i < infoList.size(); i++) {
            var prev = infoList.get(i - 1).getDate();
            var cur = infoList.get(i).getDate();
            if (!prev.plusMinutes(30).equals(cur)) {
                errors.add(infoList.get(i));
            }
        }
        return errors;
    }

    public void edit(List<Columns> errors) {
        for (int i = 1; i < infoList.size(); i++) {
            var prev = infoList.get(i - 1).getDate();
            var cur = infoList.get(i).getDate();
            if (prev.equals(cur)) {
                infoList.get(i).setDate(cur.plusMinutes(30));
            }
            if (prev.plusMinutes(30).plusDays(1).equals(cur)) {
                infoList.get(i).setDate(cur.minusDays(1));

            }
            if (prev.plusHours(1).equals(cur)) {
                infoList.get(i).setDate(cur.minusMinutes(30));
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
