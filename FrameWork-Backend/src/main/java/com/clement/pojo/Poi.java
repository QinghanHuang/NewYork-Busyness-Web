package com.clement.pojo;

import lombok.Data;

@Data
public class Poi {
    private int pid;
    private int zid;
    private String name;
    private double longitude;
    private double latitude;
    private String address;
    //TODO: photo
}
