package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.service.PoiService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<Poi> getAllPoi() {
        return mapper.getAllPoi();
    }
}
