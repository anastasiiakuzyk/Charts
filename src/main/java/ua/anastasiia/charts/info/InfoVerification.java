package ua.anastasiia.charts.info;

import ua.anastasiia.charts.info.exceptions.InfoContainsErrorsException;

import java.time.YearMonth;
import java.util.List;

public class InfoVerification {

    public static void assertAllFieldsAreSet(List<Columns> infoList) {
        int year = infoList.get(0).getYear();
        int month = infoList.get(0).getMonth();
        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        if(infoList.size() != daysInMonth * 24 * 2){
            throw new InfoContainsErrorsException("List is not full");
        }
    }
}
