package com.clement.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Poi {
    private String id;
    private String name;
    private Location location;
    private int busy;

    @Data
    public class Location{
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
