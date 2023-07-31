package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.prediction.PoiBusy;
import com.clement.pojo.prediction.ZoneBusy;
import com.clement.service.DemoPmmlModelService;
import com.clement.service.PredictionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/21 13:11
 * @Desc
 * @Version 1.0
 */

/**
 * the parameter for model
 * Double id,Double area from map
 * Double month,Double dayOfMonth,Double dayOfWeek,Double hour from date
 */
@Service
public class PredictionServiceImpl implements PredictionService {
    @Resource
    PoiMapper mapper;

    @Resource
    DemoPmmlModelService model;

    @Resource(name = "redisTemplateZoneBusys")
    RedisTemplate<String, List<ZoneBusy>> redisTemplateZoneBusys;

    @Resource(name = "redisTemplatePoiBusys")
    RedisTemplate<String, List<PoiBusy>> redisTemplatePoiBusys;

//    @Resource(name = "redisTemplatePoiBusy")
//    RedisTemplate<String, PoiBusy> redisTemplatePoiBusy;

    List<Integer> locationIds;
    List<Integer> pids;
    Map<Integer, Integer> getZid;
    Map<Integer, Double> getAreaByPid;
    Map<Integer, Double> getAreaByZid;

    @PostConstruct
    public void init() {
        locationIds = mapper.getLocationIds();
        pids = mapper.getIds();
        getZid = new HashMap<>();
        getAreaByPid = new HashMap<>();
        for (Integer pid : pids) {
            int zid = mapper.getZidById(pid);
            getZid.put(pid, zid);
            double area = mapper.getAreaByPid(pid);
            getAreaByPid.put(pid, area);
        }
        getAreaByZid = new HashMap<>();
        for (Integer locationId : locationIds) {
            double area = mapper.getAreaByZid(locationId);
            getAreaByZid.put(locationId, area);
        }
    }

    @Scheduled(fixedRate = 6* 60 * 60 * 1000) // 6 hour (in milliseconds)
    public void getAndCacheZoneBusys() {
        List<ZoneBusy> zoneBusyList = getZoneBusysFromModel();
        List<PoiBusy> poiBusyList = getPoiBusysFromModel();
        redisTemplateZoneBusys.opsForValue().set("zone-busys", zoneBusyList, 6 * 60, TimeUnit.MINUTES);
        redisTemplatePoiBusys.opsForValue().set("poi-busys", poiBusyList, 6 * 60, TimeUnit.MINUTES);
    }

    @Override
    public List<ZoneBusy> getZoneBusys(LocalDate date) {
        //check if there is cache in redis
        List<ZoneBusy> redisZoneBusys = redisTemplateZoneBusys.opsForValue().get("zone-busys");
        if (redisZoneBusys != null) {
            return redisZoneBusys;
        }
        return getZoneBusysFromModel();
    }

    public List<ZoneBusy> getZoneBusysFromModel() {
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
        List<ZoneBusy> zoneBusyList = new ArrayList<>();
        int i = 0;
        // cache busyness of 3 days
        while (i < 3) {
            double month = (double) today.getMonth().getValue();
            double dayOfMonth = (double) today.getDayOfMonth();
            double dayOfWeek = (double) today.getDayOfWeek().getValue() - 1;

            for (int hour = 0; hour < 24; hour++) {
                for (int locationId : locationIds) {
                    Double area = getAreaByZid.get(locationId);
                    int busy = model.predict((double) locationId, month, dayOfMonth, dayOfWeek, (double) hour, area);
                    LocalDateTime time = today.atTime(hour, 0);
                    zoneBusyList.add(new ZoneBusy(time, locationId, busy));
                }
            }
            i++;
            today = today.plusDays(1);
        }

        redisTemplateZoneBusys.opsForValue().set("zone-busys", zoneBusyList, 6 * 60, TimeUnit.MINUTES);

        return zoneBusyList;

    }

    @Override
    public List<PoiBusy> getPoiBusys(LocalDate date) {
        //check if there is cache in redis
        List<PoiBusy> redisPoiBusys = redisTemplatePoiBusys.opsForValue().get("poi-busys");
        if (redisPoiBusys != null) {
            return redisPoiBusys;
        }
        return getPoiBusysFromModel();
    }

    public List<PoiBusy> getPoiBusysFromModel() {
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
        List<PoiBusy> poiBusyList = new ArrayList<>();
        int i = 0;
        while(i < 3) {

            double month = (double) today.getMonth().getValue();
            double dayOfMonth = (double) today.getDayOfMonth();
            double dayOfWeek = (double) today.getDayOfWeek().getValue() - 1;


            for (int hour = 0; hour < 24; hour++) {
                for (Integer pid : pids) {
                    double zid = (double) getZid.get(pid);
                    double area = getAreaByPid.get(pid);

                    int busy = model.predict(zid, month, dayOfMonth, dayOfWeek, (double) hour, area);
                    LocalDateTime time = today.atTime(hour, 0);
                    poiBusyList.add(new PoiBusy(time, pid, busy));
                }
            }
            i++;
            today = today.plusDays(1);
        }

        redisTemplatePoiBusys.opsForValue().set("poi-busys", poiBusyList, 6 * 60, TimeUnit.MINUTES);

        return poiBusyList;
    }


    // TODO: cache
    public PoiBusy getPoiBusyById(int id, LocalDateTime time){
        double zid= (double)getZid.get(id);
        double area= getAreaByPid.get(id);
        double month=(double)time.getMonth().getValue();
        double dayOfMonth=(double)time.getDayOfMonth();
        double dayOfWeek = (double)time.getDayOfWeek().getValue()-1;
        double hour = (double)time.getHour();

        int busy =model.predict(zid,month,dayOfMonth,dayOfWeek,hour,area);
        return new PoiBusy(time,id,busy);
    }
//    public PoiBusy getPoiBusyById(int id, LocalDateTime time){
//        //check if there is cache in redis
//        PoiBusy redisPoiBusy = redisTemplatePoiBusy.opsForValue().get(String.valueOf(id) + "&" + time);
//        if(redisPoiBusy != null){
//            return redisPoiBusy;
//        }
//
//        Double zid= (double)getZid.get(id);
//        Double area= getAreaByPid.get(id);
//        Double month=(double)time.getMonth().getValue();
//        Double dayOfMonth=(double)time.getDayOfMonth();
//        Double dayOfWeek = (double)time.getDayOfWeek().getValue()-1;
//        Double hour = (double)time.getHour();
//
//        int busy = model.predict(zid,month,dayOfMonth,dayOfWeek,hour,area);
//
//        PoiBusy poiBusy = new PoiBusy(time, id ,busy);
//
//        redisTemplatePoiBusy.opsForValue().set(String.valueOf(id) + "&" + time, poiBusy, 60, TimeUnit.MINUTES);
//
//        return new PoiBusy(time,id,busy);
//    }
}
