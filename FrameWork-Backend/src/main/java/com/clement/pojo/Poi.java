package com.clement.pojo;

import lombok.Data;

@Data
public class Poi {
    private int id;
    private String name;
    private Location location;

    @Data
    public static class Location{
        private double lng;
        private double lat;
    }

    public void setLat(double lat){
        if (location == null) {
            location = new Location();
        }
        location.setLat(lat);
    }

    public void setLng(double lng){
        if (location == null) {
            location = new Location();
        }
        location.setLng(lng);
    }
}
