package ua.anastasiia.charts.info;

import java.util.*;

public class FixedInfo {
    static List<Map<Columns, Columns>> errorsWithFixed;
    static List<List<Columns>> allInfo = new ArrayList<>();


    public static void main(String[] args) {
        String mainDirectory = "БАЗА РЕГІОНІВ";
        FixedInfo.processInfo(Paths.getFilesPaths(mainDirectory));
        List<Map<Columns, Columns>> errorsWithFixed = FixedInfo.getErrorsWithFixed();
        for (Map<Columns, Columns> errorsOfRegion : errorsWithFixed) {
            System.out.println(Arrays.toString(errorsOfRegion.entrySet().toArray()));
        }
    }

    public static List<Map<Columns, Columns>> getErrorsWithFixed() {
        return errorsWithFixed;
    }

    public static List<List<Columns>> getAllInfo() {
        return allInfo;
    }

    public static List<List<Columns>> getAllProcessedInfo(String mainDirectory) {
        processInfo(Paths.getFilesPaths(mainDirectory));
        return allInfo;
    }

    public static void processInfo(String[] filesPaths) {
        allInfo = new ArrayList<>();
        errorsWithFixed = new ArrayList<>();

        List<Columns> infoByRegion = new ArrayList<>();
        Map<Columns, Columns> errorsByRegion = new HashMap<>();
        for (String filePath : filesPaths) {
            List<Columns> infoList = ExcelUtil.extractInfo(filePath);
            Interpolation.setMethod(Interpolation.Method.Linear);
            int month = infoList.get(0).getMonth();
            InfoVerification.assertAllFieldsAreSet(infoList);
            Temperature temperature = new Temperature(infoList);
            Time time = new Time(infoList);
            Wind wind = new Wind(infoList);

            boolean edited = false;
            ErrorsFixer[] fixers = {time, temperature, wind};
            for (ErrorsFixer fixer : fixers) {
                List<Columns> errors = fixer.getErrors();
                List<Columns> errorsForMap = new ArrayList<>();
                for (Columns error : errors) {
                    errorsForMap.add(error.myClone());
                }
                if (!errors.isEmpty()) {
                    edited = true;
                    fixer.edit(errors);
                    List<Columns> processedRows = fixer.getFixedRows(errors);
                    for (int i = 0; i < errorsForMap.size(); i++) {
                        errorsByRegion.put(errorsForMap.get(i), processedRows.get(i));
                    }
                    assert !fixer.getErrors().isEmpty();
                }
            }
            if (edited) {
                List<Columns> complitedEditedMonth = ErrorsFixer.getCompletedList();
                infoByRegion.addAll(complitedEditedMonth);
            } else {
                infoByRegion.addAll(infoList);
            }
            //todo
            if (month == 12) {
                allInfo.add(infoByRegion);
                TreeMap<Columns, Columns> sortedErrorsByRegion = new TreeMap<>(errorsByRegion);
                errorsWithFixed.add(sortedErrorsByRegion);
                infoByRegion = new ArrayList<>();
                errorsByRegion = new HashMap<>();
            }

        }
    }

}
