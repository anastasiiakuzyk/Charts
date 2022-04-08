package ua.anastasiia.charts.info;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Columns implements Comparable<Columns>{
    private String region;
    private int year;
    private int month;
    private LocalDateTime date;
    private Double temperature;
    private String windDirection;
    private Double windSpeed;

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getDayTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddHHmm");
        return Integer.parseInt(date.format(formatter));
    }

    public Columns(String region, int year, int month, LocalDateTime date, Double temperature, String windDirection, Double windSpeed) {
        this.region = region;
        this.year = year;
        this.month = month;
        this.date = date;
        this.temperature = temperature;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
    }

    public Columns() {
    }

    public int getDay() {
        return date.getDayOfMonth();
    }

    public String getTime() {
        return date.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public void setDate(String fileName, int day, int hours, int minutes) {
        year = Integer.parseInt(fileName.substring(0, 4));
        month = Integer.parseInt(fileName.substring(5, fileName.indexOf(".xlsx")));
        date = LocalDateTime.of(year, month, day, hours, minutes);
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        BigDecimal bd = new BigDecimal(temperature).setScale(1, RoundingMode.HALF_UP);
        this.temperature = bd.doubleValue();
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        BigDecimal bd = new BigDecimal(windSpeed).setScale(1, RoundingMode.HALF_UP);
        this.windSpeed = bd.doubleValue();
    }

    @Override
    public String toString() {
        return "Row: " +
                "region='" + region + '\'' +
                ", date=" + date +
                ", temperature=" + temperature +
                ", windDirection='" + windDirection + '\'' +
                ", windSpeed=" + windSpeed;
    }

    protected Columns myClone() {
        return new Columns(region, year, month, date, temperature, windDirection, windSpeed);
    }


    @Override
    public int compareTo(Columns o) {
        return this.getDate().compareTo(o.getDate());
    }
}
