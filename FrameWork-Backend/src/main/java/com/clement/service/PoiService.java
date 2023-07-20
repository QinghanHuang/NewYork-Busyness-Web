package com.clement.service;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.TaxiZone;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface PoiService {
    PoiInfo getPoiInfoById(int id) throws JsonProcessingException;
    List<Poi> getAllPois() throws JsonProcessingException;
    List<TaxiZone> getAllTaxiZones() throws JsonProcessingException;
}
