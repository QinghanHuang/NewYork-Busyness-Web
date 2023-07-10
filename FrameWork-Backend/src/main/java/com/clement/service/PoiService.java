package com.clement.service;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.RestBean;
import com.clement.pojo.TaxiZone;

import java.util.List;

public interface PoiService {
    RestBean<PoiInfo> getPoiInfoById(String id);
    RestBean<List<Poi>> getAllPoi();
    RestBean<List<TaxiZone>> getAllTaxiZone();
}
