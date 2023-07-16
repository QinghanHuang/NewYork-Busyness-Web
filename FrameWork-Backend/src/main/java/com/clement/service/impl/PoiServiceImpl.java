package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.TaxiZone;
import com.clement.service.PoiService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PoiServiceImpl implements PoiService {
    @Resource
    PoiMapper mapper;

    @Override
    public PoiInfo getPoiInfoById(String id) {
            return mapper.getPoiInfoById(id);
    }

    @Override
    public List<Poi> getPoiLocation() {
            return mapper.getAllPoi();
    }
    @Override
    public List<TaxiZone> getTaxiZoneLocation() {
        return mapper.getAllTaxiZone();
    }

}
