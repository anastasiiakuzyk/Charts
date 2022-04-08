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