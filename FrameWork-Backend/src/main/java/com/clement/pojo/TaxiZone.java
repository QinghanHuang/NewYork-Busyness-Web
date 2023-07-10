package com.clement.pojo;

import lombok.Data;

@Data
public class TaxiZone {
    private int zid;
    private Location location;
    private double radius;
    private int busy;

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


