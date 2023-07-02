package com.clement.service;

import com.clement.pojo.Poi;

import java.util.List;

public interface PoiService {
    public Poi getPoiById(int pid);

    public List<Poi> getAllPoi();
}
