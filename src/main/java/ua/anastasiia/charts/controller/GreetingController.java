package ua.anastasiia.charts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.anastasiia.charts.info.Columns;
import ua.anastasiia.charts.info.RegionSelect;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
        for (Columns row : selectedFull) {
            LocalDateTime date = row.getDate();
            ZonedDateTime zdt = date.atZone(ZoneId.systemDefault());
            long milli = zdt.toInstant().toEpochMilli();
            datesMil.add(milli);
            temps.add(row.getTemperature());
        }
        datesMil.sort(Comparator.naturalOrder());
        Long[] datesMilArray = new Long[datesMil.size()];
        Double[] tempsArray = new Double[temps.size()];
        for (int i = 0; i < datesMilArray.length; i++) {
            datesMilArray[i] = datesMil.get(i);
            tempsArray[i] = temps.get(i);
        }
        model.addAttribute("datesArray", datesMilArray);
        model.addAttribute("tempsArray", tempsArray);
        return "lineGraph";
    }

}