package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.service.PredictionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PredictionServiceImpl implements PredictionService {
    @Resource
    PoiMapper mapper;

    List<HashMap<Integer, Integer>> zoneBusyList = new ArrayList<>();
    List<HashMap<Integer, Integer>> poiBusyList = new ArrayList<>();

    @Override
    public int getBusyById(int id, int hour){
        int locationId = mapper.getZidById(id);
        if (zoneBusyList.isEmpty()) predictZoneBusy();
        return zoneBusyList.get(hour).get(locationId);
    }

    @Override
    public List<HashMap<Integer, Integer>> getZoneBusys(){
        if (zoneBusyList.isEmpty() ) predictZoneBusy();
        return zoneBusyList;
    }

    //TODO: response time
    @Override
    public List<HashMap<Integer, Integer>> getPoiBusys(){
        if (poiBusyList.isEmpty()) predictPoiBusy();
        return poiBusyList;
    }

    //TODO: deploy the model here and receive the targetTime as an input to the model
    public void predictZoneBusy(){
        List<Integer> locationIds = mapper.getLocationIds();
        Random random = new Random();
        for(int hour = 0; hour < 24; hour++){
            HashMap<Integer, Integer> zoneBusyMap = new HashMap<>();
            for(int locationId : locationIds){
                int busy = random.nextInt(5) + 1;
                zoneBusyMap.put(locationId, busy);
            }
            zoneBusyList.add(zoneBusyMap);
        }
    }

    public int predictBusyByZid(int locationId, int hour){
        return zoneBusyList.get(hour).get(locationId);
    }

    public void predictPoiBusy(){
        List<Integer> ids = mapper.getIds();
        for (int hour = 0; hour < 24; hour++) {
            HashMap<Integer, Integer> poiBusyMap = new HashMap<>();
            for (int id : ids) {
                int locationId = mapper.getZidById(id);
                int busy = predictBusyByZid(locationId, hour);
                poiBusyMap.put(id, busy);
            }
            poiBusyList.add(poiBusyMap);
        }
    }
}
