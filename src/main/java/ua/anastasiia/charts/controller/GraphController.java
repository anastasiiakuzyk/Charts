package ua.anastasiia.charts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.anastasiia.charts.info.Columns;
import ua.anastasiia.charts.info.FixedInfo;
import ua.anastasiia.charts.info.Paths;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GraphController {

    @GetMapping("/displayBarGraph")
    public String barGraph(Model model) {
        Map<String, Integer> surveyMap = new LinkedHashMap<>();
        surveyMap.put("Java", 40);
        surveyMap.put("Dev oops", 25);
        surveyMap.put("Python", 20);
        surveyMap.put(".Net", 15);
        String str = "2017,2\n" +
                "2018,2\n" +
                "2019,3\n" +
                "2020,3\n" +
                "2021,2\n" +
                "2022,3";
        model.addAttribute("surveyMap", surveyMap);
        model.addAttribute("str", str);
        return "barGraph";
    }

    @GetMapping("/displayFixedErrors")
    public String fixedErrors(Model model) {

//        String mainDirectory = "БАЗА РЕГІОНІВ";
        String mainDirectory = "БАЗА РЕГІОНІВ1";
        FixedInfo.processInfo(Paths.getFilesPaths(mainDirectory));
        List<Map<Columns, Columns>> errorsWithFixed = FixedInfo.getErrorsWithFixed();
        String[] regions = new String[errorsWithFixed.size()];
        for (int i = 0; i < regions.length; i++) {
            regions[i] = errorsWithFixed.get(i).values().toArray(new Columns[0])[0].getRegion();
        }

        model.addAttribute("errorsWithFixed", errorsWithFixed);
        model.addAttribute("namesOfRegions", regions);
        return "fixedErrors";
    }
}