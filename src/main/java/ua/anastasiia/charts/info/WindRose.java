package ua.anastasiia.charts.info;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WindRose {
    List<Columns> info;
    List<Integer> angles;
    List<String> speedsRanges;
    List<Double> speeds;
    List<Double> percents;
    Double percentsCalm;
    private int calmTimes;
    private double maxSpeed;
    private double avgSpeed;

    public WindRose(List<Columns> info) {
        this.info = info;
    }

    public String getData() {
        String result = "angle,speed,percent\n";
        setAngles();
        countCalm();
        setPercents();
        setSpeeds();
        setSpeedRanges();
        BigDecimal percent = new BigDecimal(calmTimes / ((double) angles.size() + calmTimes)).setScale(3, RoundingMode.HALF_UP);
        percentsCalm = percent.doubleValue();
        for (int i = 0; i < angles.size(); i++) {
            result += angles.get(i) + "," + speedsRanges.get(i) + "," + percents.get(i) + "\n";
        }
        return result;
    }

    public Double getPercentsCalm() {
        return percentsCalm;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    private void setAngles() {
        angles = new ArrayList<>();
        String[] windDirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};        //, "UNSTEADY", "CALM"
        int[] windAngles = {360, 45, 90, 135, 180, 225, 270, 315};
        for (Columns row : info) {
            if (!row.getWindDirection().equals("UNSTEADY") && !row.getWindDirection().equals("CALM")) {
                for (int j = 0; j < windDirs.length; j++) {
                    if (row.getWindDirection().equals(windDirs[j])) {
                        angles.add(windAngles[j]);
                        break;
                    }
                }
            }
        }
    }

    private void setSpeeds() {
        speeds = new ArrayList<>();
        for (Columns row : info) {
            if (!row.getWindDirection().equals("UNSTEADY") && !row.getWindDirection().equals("CALM")) {
                speeds.add(row.getWindSpeed());
            }
        }
        maxSpeed = Collections.max(speeds);
        BigDecimal avg = BigDecimal.valueOf(speeds.stream().mapToDouble(a -> a).average().orElse(0)).setScale(3, RoundingMode.HALF_UP);
        avgSpeed = avg.doubleValue();
    }

    private void setPercents() {
        percents = new ArrayList<>();
        double size = angles.size() + calmTimes;
        Map<Integer, Long> countDirectionsRepeating = angles.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        for (Integer angle : angles) {
            BigDecimal percent = new BigDecimal(countDirectionsRepeating.get(angle) / size).setScale(3, RoundingMode.HALF_UP);
            percents.add(percent.doubleValue() / 100);
        }
    }

    private void countCalm() {
        for (Columns row : info) {
            if (row.getWindSpeed() == 0) {
                calmTimes++;
            }
        }
    }

    private String getSpeedRange(Double curSpeed) {
        String windSpeed = "";

        int[] ranges = {0, 3, 4, 5, 7};
        if (curSpeed >= ranges[0] && curSpeed < ranges[1]) {
            windSpeed = ranges[0] + " - " + ranges[1];
        } else if (curSpeed >= ranges[1] && curSpeed < ranges[2]) {
            windSpeed = ranges[1] + " - " + ranges[2];
        } else if (curSpeed >= ranges[2] && curSpeed < ranges[3]) {
            windSpeed = ranges[2] + " - " + ranges[3];
        } else if (curSpeed >= ranges[3] && curSpeed < ranges[4]) {
            windSpeed = ranges[3] + " - " + ranges[4];
        } else if (curSpeed >= ranges[4]) {
            windSpeed = ranges[4] + "+";
        }
        return windSpeed;
    }

    private void setSpeedRanges() {
        speedsRanges = new ArrayList<>();
        for (Double speed : speeds) {
            speedsRanges.add(getSpeedRange(speed));
        }
    }
}
