package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.TaxiZone;
import com.clement.service.PoiService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PoiServiceImpl implements PoiService {
    @Resource
    PoiMapper mapper;

    @Resource
    RedisTemplate<String, Map<String,List<Integer>>> redisTemplate;

    //TODO: current busy or 24 hours busy
    @Override
    public PoiInfo getPoiInfoById(int id) {
        PoiInfo poiInfo= mapper.getPoiInfoById(id);
        String zid = String.valueOf(mapper.getZidById(id));
        List<Integer> busyList = getBusyMap().get(zid);
        poiInfo.setBusy(busyList.get(0));
        return poiInfo;
    }

    @Override
    public List<Poi> getAllPois() {
        List<Poi> pois = mapper.getAllPois();
        for (Poi poi: pois) {
            int pid = poi.getId();
            String zid = String.valueOf(mapper.getZidById(pid));
            List<Integer> busyList = getBusyMap().get(zid);
            poi.setBusy(busyList);
        }
        return pois;
    }
    @Override
    public List<TaxiZone> getAllTaxiZones() {
        List<TaxiZone> taxiZones = mapper.getAllTaxiZones();
        for (TaxiZone taxiZone : taxiZones){
            String zid = String.valueOf(taxiZone.getLocationId());
            List<Integer> busyList = getBusyMap().get(zid);
            taxiZone.setBusy(busyList);
        }
        return taxiZones;
    }
    @Override
    public void updateBusy(){
        // generate 24 random numbers for each taxi zone's busyness
        List<Integer> locationIds = mapper.getLocationIds();
        Random random = new Random();

        Map<String,List<Integer>> busyMap = new HashMap<>();

        for(int locationId : locationIds){
            List<Integer> busyList = new ArrayList<>();
            String zid = String.valueOf(locationId);
            for(int i = 0; i < 24; i++){
                int busy = random.nextInt(5) + 1;
                busyList.add(busy);
            }
            busyMap.put(zid, busyList);
        }
        String key = "busy";
        redisTemplate.opsForValue().set(key, busyMap);
    }

    @Override
    public Map<String, List<Integer>> getBusyMap(){
        Map<String, List<Integer>> busyMap = redisTemplate.opsForValue().get("busy");
        if(busyMap != null){
            return busyMap;
        } else{
            updateBusy();
            return redisTemplate.opsForValue().get("busy");
        }
    }

    @Override
    public int getZidById(int id) {
        return mapper.getZidById(id);
    }
}
