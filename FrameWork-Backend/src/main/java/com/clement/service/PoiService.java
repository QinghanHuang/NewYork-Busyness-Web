package com.clement.service;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;

import java.util.List;

public interface PoiService {
    public PoiInfo getPoiInfoById(String id);

    public List<Poi> getAllPoi();
}
