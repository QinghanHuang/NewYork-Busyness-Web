package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.RestBean;
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
    public RestBean<PoiInfo> getPoiInfoById(String id) {
        return RestBean.succcess(mapper.getPoiInfoById(id));
    }

    @Override
    public RestBean<List<Poi>> getAllPoi() {
        return RestBean.succcess(mapper.getAllPoi());
    }
    @Override
    public RestBean<List<TaxiZone>> getAllTaxiZone() {
        return RestBean.succcess(mapper.getAllTaxiZone());
    }

}
