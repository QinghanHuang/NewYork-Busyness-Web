package com.clement.pojo;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class TaxiZone implements Serializable {
    private int locationId;
    private Location location;
    private double radius;
    private List<Integer> busy;

    @Data
    public static class Location {
        private double circleLng;
        private double circleLat;
    }

    public void setCircleLng(double circleLng) {
        if (location == null) {
            location = new TaxiZone.Location();
        }
        location.setCircleLng(circleLng);
    }

    public void setCircleLat(double circleLat) {
        if (location == null) {
            location = new TaxiZone.Location();
        }
        location.setCircleLat(circleLat);
    }
}


