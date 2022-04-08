package ua.anastasiia.charts.info;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegionSelect {
    String region;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime start;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime end;
    List<Columns> selected;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public List<Columns> getSelected() {
        return selected;
    }

    public void setSelected(List<List<Columns>> allInfo) {
        selected = new ArrayList<>();
        for (List<Columns> infoOfRegion : allInfo) {
            if (region.equals(infoOfRegion.get(0).getRegion())) {
                for (Columns info : infoOfRegion) {
                    LocalDateTime curDate = info.getDate();
                    if ((start.isEqual(curDate) || start.isBefore(curDate)) && (end.isEqual(curDate) || end.isAfter(curDate))) {
                        selected.add(info);
                    }
                }
            }
        }
    }
}
