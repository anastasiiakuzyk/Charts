package ua.anastasiia.charts.info;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ErrorsFixer {
    static List<Columns> completedList;
    List<Columns> infoList;

    public ErrorsFixer(List<Columns> infoList) {
        this.infoList = infoList;
    }

    public static List<Columns> getCompletedList() {
        return completedList;
    }

    public abstract List<Columns> getErrors();

    public String errorsToString() {
        List<Columns> errors = getErrors();
        String message = getClass().getSimpleName() + " is not correct:";
        for (Columns row : errors) {
            message += "\n" + row.toString();
        }
        return message;
    }

    public void edit(List<Columns> errors) {
        DataSet resultSet = interpolation();
        completedList = getCompletedList(resultSet);
    }

    protected List<Columns> getFixedRows(List<Columns> errors) {
        List<Columns> fixedRows = new ArrayList<>();
        for (Columns row : completedList) {
            for (Columns rowError : errors) {
                if (rowError.getDate().equals(row.getDate())) {
                    fixedRows.add(row);
                }
            }
        }
        return fixedRows;
    }

    protected abstract double[] getY(List<Columns> list);

    public DataSet interpolation() {
        List<Columns> list = new ArrayList<>(infoList);
        List<Columns> errors = getErrors();
        list.removeAll(errors);
        double[] y = getY(list);
        double[] x = new double[list.size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = list.get(i).getDayTime();
        }
        ArrayList<Integer> inXListInBounds = new ArrayList<>();

        for (Columns row : errors) {
            inXListInBounds.add(row.getDayTime());
        }

        ArrayList<Integer> inXList = new ArrayList<>(inXListInBounds);
        ArrayList<Integer> inXListOutOfBounds = new ArrayList<>();

        for (Integer inX : inXListInBounds) {
            if (inX > x[x.length - 1]) {
                inXListOutOfBounds.add(inX);
            }
        }

        inXListInBounds.removeAll(inXListOutOfBounds);
        double[] inXInBounds = Arrays.stream(inXListInBounds.stream().mapToInt(Integer::intValue).toArray()).asDoubleStream().toArray();

        DataSet resultSet = Interpolation.interpolate(x, y, inXInBounds);
        double[] inX = Arrays.stream(inXList.stream().mapToInt(Integer::intValue).toArray()).asDoubleStream().toArray();
        resultSet.setInX(inX);
        double[] inYInBounds = resultSet.getInY();

        double[] inY = new double[inYInBounds.length + inXListOutOfBounds.size()];
        System.arraycopy(inYInBounds, 0, inY, 0, inYInBounds.length);

        for (int i = inYInBounds.length; i < inY.length; i++) {
            inY[i] = inYInBounds[inYInBounds.length - 1];
        }

        resultSet.setInY(inY);
        return resultSet;
    }

    protected abstract void changeToInterpolated(DataSet set, Columns row, int j);

    private List<Columns> getCompletedList(DataSet set) {
        List<Columns> completedList = new ArrayList<>();
        int[] interpolatedDate = new int[set.getInX().length];
        for (int i = 0; i < set.getInX().length; i++) {
            interpolatedDate[i] = (int) set.getInX()[i];
        }
        for (Columns row : infoList) {
            for (int j = 0; j < interpolatedDate.length; j++) {
                if (interpolatedDate[j] == row.getDayTime()) {
                    changeToInterpolated(set, row, j);
                }
            }
            completedList.add(row);
        }
        return completedList;
    }

}
