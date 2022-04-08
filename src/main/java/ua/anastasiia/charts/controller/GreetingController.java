package ua.anastasiia.charts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.anastasiia.charts.info.Columns;
import ua.anastasiia.charts.info.RegionSelect;
import ua.anastasiia.charts.info.WindRose;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static ua.anastasiia.charts.info.FixedInfo.getAllProcessedInfo;

@Controller
public class GreetingController {

    List<List<Columns>> allInfo = getAllProcessedInfo("БАЗА РЕГІОНІВ1");

    @GetMapping("/greeting")
    public String greetingForm(Model model) {
        List<String> options = new ArrayList<>();
        for (List<Columns> info : allInfo) {
            options.add(info.get(0).getRegion());
        }

        model.addAttribute("select", new RegionSelect());
        model.addAttribute("options", options);
        return "greeting";
    }

    @PostMapping("/greeting")
    public String greetingSubmit(@ModelAttribute RegionSelect selected, Model model) {
        selected.setSelected(allInfo);
        List<Columns> selectedFull = selected.getSelected();
        List<Long> datesMil = new ArrayList<>();
        List<Double> temps = new ArrayList<>();
        List<Double> windSpeeds = new ArrayList<>();
        for (Columns row : selectedFull) {
            LocalDateTime date = row.getDate();
            ZonedDateTime zdt = date.atZone(ZoneOffset.UTC);
            long milli = zdt.toInstant().toEpochMilli();
            datesMil.add(milli);
            temps.add(row.getTemperature());
            windSpeeds.add(row.getWindSpeed());
        }
        List<Integer> tempsRounded = new ArrayList<>();
        for (Double temp : temps) {
            tempsRounded.add((int) Math.round(temp));
        }
        List<Integer> windSpeedsRounded = new ArrayList<>();
        for (Double windSpeed : windSpeeds) {
            windSpeedsRounded.add((int) Math.round(windSpeed));
        }

        TreeMap<Integer, Long> sortedCountTemps = new TreeMap<>(tempsRounded.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting())));
        sortedCountTemps.replaceAll((k, v) -> v = v / 2);
        TreeMap<Integer, Long> sortedCountWindSpeeds = new TreeMap<>(windSpeedsRounded.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting())));
        sortedCountWindSpeeds.replaceAll((k, v) -> v = v / 2);

        datesMil.sort(Comparator.naturalOrder());
        Long[] datesMilArray = new Long[datesMil.size()];
        Double[] tempsArray = new Double[temps.size()];
        for (int i = 0; i < datesMilArray.length; i++) {
            datesMilArray[i] = datesMil.get(i);
            tempsArray[i] = temps.get(i);
        }
        WindRose windRose = new WindRose(selectedFull);
        String dataForWindRose = windRose.getData();
        Double percentsCalm = windRose.getPercentsCalm();
        double avgSpeed = windRose.getAvgSpeed();
        model.addAttribute("datesArray", datesMilArray);
        model.addAttribute("dataForWindRose", dataForWindRose);
        model.addAttribute("percentsCalm", percentsCalm);
        model.addAttribute("avgSpeed", avgSpeed);
        model.addAttribute("countTemps", sortedCountTemps);
        model.addAttribute("countWindSpeeds", sortedCountWindSpeeds);
        model.addAttribute("tempsArray", tempsArray);
        return "lineGraph";
    }

}