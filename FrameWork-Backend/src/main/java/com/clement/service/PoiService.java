package com.clement.service;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.TaxiZone;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface PoiService {
    PoiInfo getPoiInfoById(String id) throws JsonProcessingException;
    List<Poi> getPoiLocation() throws JsonProcessingException;
    List<TaxiZone> getTaxiZoneLocation() throws JsonProcessingException;
}
