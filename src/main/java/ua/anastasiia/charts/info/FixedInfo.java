package ua.anastasiia.charts.info;

import java.io.*;
import java.util.*;

public class FixedInfo {
    private static List<Map<Columns, Columns>> errorsWithFixed;
    private static List<List<Columns>> allInfo = new ArrayList<>();

    public static List<Map<Columns, Columns>> getErrorsWithFixed() {
        return errorsWithFixed;
    }

    public static List<List<Columns>> getAllInfo() {
        return allInfo;
    }

    public static List<List<Columns>> getAllProcessedInfo(String mainDirectory) {
        processInfoOptimised(Paths.getFilesPaths(mainDirectory));
        return allInfo;
    }

    public static void processInfoOptimised(String[] filesPaths){
        File file = new File("allInfo.dat");
        if (!file.exists()) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                processInfo(filesPaths);
                out.writeObject(allInfo);
                out.writeObject(errorsWithFixed);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                allInfo = (List<List<Columns>>) in.readObject();
                errorsWithFixed = (List<Map<Columns, Columns>>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void processInfo(String[] filesPaths) {
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
            WindSpeed windSpeed = new WindSpeed(infoList);

            boolean edited = false;
            ErrorsFixer[] fixers = {time, temperature, wind, windSpeed};
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
