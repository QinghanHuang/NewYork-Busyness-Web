package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.Poi;
import com.clement.service.PoiService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PoiServiceImpl implements PoiService {
    @Resource
    PoiMapper mapper;
    @Override
    public Poi getPoiById(int pid) {
        return mapper.getPoiById(pid);
    }

    @Override
    public List<Poi> getAllPoi() {
        return mapper.getAllPoi();
    }
}
