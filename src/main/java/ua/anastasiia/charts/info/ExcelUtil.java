package ua.anastasiia.charts.info;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.anastasiia.charts.info.exceptions.InfoContainsErrorsException;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

public final class ExcelUtil {

    public static List<Columns> extractInfo(String filePath) {
        List<Columns> infoList = new ArrayList<>();
        Workbook wb = null;

        try {
            wb = new XSSFWorkbook(new FileInputStream(filePath));
            Sheet sheet = wb.getSheetAt(0);

            for (int i = 5; i <= 10; i++) {
                deleteColumn(sheet, i);
            }

            boolean skipHeader = true;

            for (Row row : sheet) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                List<Cell> cells = new ArrayList<>();

                int lastColumn = Math.max(row.getLastCellNum(), 5);
                for (int cn = 0; cn < lastColumn; cn++) {
                    Cell c = row.getCell(cn, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    cells.add(c);
                }
                Columns info = extractInfoFromCell(cells, filePath);
                if (info == null) {
                    continue;
                }
                infoList.add(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return infoList;
    }

    private static Columns extractInfoFromCell(List<Cell> cells, String filePath) {
        Columns row = new Columns();
        row.setRegion(Paths.getRegionFromPath(filePath));

        Cell dayCell = cells.get(0);
        int day = 0;
        if (dayCell != null) {
            if (dayCell.getCellType() == CellType.NUMERIC) {
                day = ((int) dayCell.getNumericCellValue());
            }
        } else {
            return null;
        }

        Cell timeCell = cells.get(1);
        int hours = 0;
        int minutes = 0;
        if (timeCell != null) {
            hours = (timeCell.getLocalDateTimeCellValue().getHour());
            minutes = (timeCell.getLocalDateTimeCellValue().getMinute());
        }

        try {
            row.setDate(Paths.getFileNameFromPath(filePath), day, hours, minutes);
        } catch (DateTimeException e) {
            return null;
        }

        Cell tCell = cells.get(2);
        if (tCell != null) {
            if (tCell.getCellType() == CellType.NUMERIC) {
                row.setTemperature(tCell.getNumericCellValue());
            }
        }

        Cell ddCell = cells.get(3);
        if (ddCell != null) {
            row.setWindDirection(ddCell.getStringCellValue());
        }

        Cell ffCell = cells.get(4);
        if (ffCell != null) {
            if (ffCell.getCellType() == CellType.NUMERIC) {
                row.setWindSpeed(ffCell.getNumericCellValue());
            }
        }

        localiseWindDirection(row);

        return row;
    }

    private static void localiseWindDirection(Columns row) {
        String[] WindDirRus = {"Северный", "С-В", "Восточный", "Ю-В", "Южный", "Ю-З", "Западный", "С-З", "Переменный", null};
        String[] WindDirEng = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "UNSTEADY", "CALM"};
        if (row.getWindDirection() == null && row.getWindSpeed() == 0) {
            row.setWindDirection(WindDirEng[9]);
            return;
        }

        if (row.getWindDirection() == null) {
            row.setWindDirection(null);
            return;
        }

        for (int i = 0; i < WindDirEng.length; i++) {
            if (row.getWindDirection().equals(WindDirRus[i])) {
                row.setWindDirection(WindDirEng[i]);
                return;
            }
        }

        throw new InfoContainsErrorsException("No such wind direction");
    }

    private static void deleteColumn(Sheet sheet, int columnToDelete) {
        for (int rId = 0; rId <= sheet.getLastRowNum(); rId++) {
            Row row = sheet.getRow(rId);
            for (int cID = columnToDelete; cID < row.getLastCellNum(); cID++) {
                Cell cOld = row.getCell(cID);
                if (cOld != null) {
                    row.removeCell(cOld);
                }
                Cell cNext = row.getCell(cID + 1);
                if (cNext != null) {
                    Cell cNew = row.createCell(cID, cNext.getCellType());
                    cloneCell(cNew, cNext);
                    sheet.setColumnWidth(cID, sheet.getColumnWidth(cID + 1));
                }
            }
        }
    }

    private static void cloneCell(Cell cNew, Cell cOld) {
        cNew.setCellComment(cOld.getCellComment());
        cNew.setCellStyle(cOld.getCellStyle());

        switch (cNew.getCellType()) {
            case BOOLEAN: {
                cNew.setCellValue(cOld.getBooleanCellValue());
                break;
            }
            case NUMERIC: {
                cNew.setCellValue(cOld.getNumericCellValue());
                break;
            }
            case STRING: {
                cNew.setCellValue(cOld.getStringCellValue());
                break;
            }
            case ERROR: {
                cNew.setCellValue(cOld.getErrorCellValue());
                break;
            }
            case FORMULA: {
                cNew.setCellFormula(cOld.getCellFormula());
                break;
            }
            default:
                System.exit(-1);
        }
    }

}