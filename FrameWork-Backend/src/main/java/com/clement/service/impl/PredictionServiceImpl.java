package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.prediction.PoiBusy;
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

//    @Resource(name = "redisTemplateZoneBusys")
//    RedisTemplate<String, List<ZoneBusy>> redisTemplateZoneBusys;

    @Resource(name = "redisTemplatePoiBusys")
    RedisTemplate<String, List<PoiBusy>> redisTemplatePoiBusys;

    List<Integer> locationIds;
    List<Integer> pids;
    Map<Integer, Integer> getZid;
    Map<Integer, Double> getAreaByPid;
    Map<Integer,Double> getPerimeterByPid;

    @PostConstruct
    public void init() {
        locationIds = mapper.getLocationIds();
        pids = mapper.getIds();
        getZid = new HashMap<>();
        getAreaByPid = new HashMap<>();
        getPerimeterByPid=new HashMap<>();
        for (Integer pid : pids) {
            int zid = mapper.getZidById(pid);
            getZid.put(pid, zid);
            double area = mapper.getAreaByPid(pid);
            getAreaByPid.put(pid, area);
            double perimeter=mapper.getPerimeterByPid(pid);
            getPerimeterByPid.put(pid,perimeter);
        }
    }

    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // 6 hour (in milliseconds)
    public void getAndCacheZoneBusys() {
        cachePoiBusys();
    }

//    @Override
//    public List<ZoneBusy> getZoneBusys(LocalDate date) {
//        //check if there is cache in redis
//        List<ZoneBusy> redisZoneBusys = redisTemplateZoneBusys.opsForValue().get("zone-busys");
//        if (redisZoneBusys != null) {
//            return redisZoneBusys;
//        }
//        return getZoneBusysFromModel();
//    }

//    public List<ZoneBusy> getZoneBusysFromModel() {
//        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));
//        List<ZoneBusy> zoneBusyList = new ArrayList<>();
//        int i = 0;
//        // cache busyness of 3 days
//        while (i < 3) {
//            double month = (double) today.getMonth().getValue();
//            double dayOfMonth = (double) today.getDayOfMonth();
//            double dayOfWeek = (double) today.getDayOfWeek().getValue() - 1;
//
//            for (int hour = 0; hour < 24; hour++) {
//                for (int locationId : locationIds) {
//                    Double area = getAreaByZid.get(locationId);
//                    int busy = model.predict((double) locationId, month, dayOfMonth, dayOfWeek, (double) hour, area);
//                    LocalDateTime time = today.atTime(hour, 0);
//                    zoneBusyList.add(new ZoneBusy(time, locationId, busy));
//                }
//            }
//            i++;
//            today = today.plusDays(1);
//        }
//
//        redisTemplateZoneBusys.opsForValue().set("zone-busys", zoneBusyList, 6 * 60, TimeUnit.MINUTES);
//
//        return zoneBusyList;
//
//    }

    @Override
    public List<PoiBusy> getPoiBusys(LocalDate date) {
        //check if there is cache in redis
        List<PoiBusy> redisPoiBusys = redisTemplatePoiBusys.opsForValue().get("poi-busys:" + date);
        if (redisPoiBusys != null) {
            return redisPoiBusys;
        }

        double month = (double) date.getMonth().getValue();
        double dayOfMonth = (double) date.getDayOfMonth();
        double dayOfWeek = (double) date.getDayOfWeek().getValue() - 1;

        List<PoiBusy> poiBusyList = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            for (Integer pid : pids) {
                double zid = (double) getZid.get(pid);
                double area = getAreaByPid.get(pid);
                double perimeter= getPerimeterByPid.get(pid);

                int busy = model.predict(zid, month, dayOfMonth, dayOfWeek, (double) hour, area,perimeter);

                LocalDateTime time = date.atTime(hour, 0);
                poiBusyList.add(new PoiBusy(time, pid, busy));
            }
        }
        redisTemplatePoiBusys.opsForValue().set("poi-busys:" + date, poiBusyList, 6 * 60, TimeUnit.MINUTES);
        return poiBusyList;
    }

    public void cachePoiBusys() {
        LocalDate today = LocalDate.now(ZoneId.of("America/New_York"));

        int i = 0;
        while (i < 3) {
            List<PoiBusy> poiBusyList = new ArrayList<>();

            double month = (double) today.getMonth().getValue();
            double dayOfMonth = (double) today.getDayOfMonth();
            double dayOfWeek = (double) today.getDayOfWeek().getValue() - 1;


            for (int hour = 0; hour < 24; hour++) {
                for (Integer pid : pids) {
                    double zid = (double) getZid.get(pid);
                    double area = getAreaByPid.get(pid);
                    double perimeter= getPerimeterByPid.get(pid);

                    int busy = model.predict(zid, month, dayOfMonth, dayOfWeek, (double) hour, area,perimeter);
                    LocalDateTime time = today.atTime(hour, 0);
                    poiBusyList.add(new PoiBusy(time, pid, busy));
                }
            }
            redisTemplatePoiBusys.opsForValue().set("poi-busys:" + today, poiBusyList, 6 * 60, TimeUnit.MINUTES);
            i++;
            today = today.plusDays(1);
        }
    }

    public PoiBusy getPoiBusyById(int id, LocalDateTime time) {
        double zid = (double) getZid.get(id);
        double area = getAreaByPid.get(id);
        double month = (double) time.getMonth().getValue();
        double dayOfMonth = (double) time.getDayOfMonth();
        double dayOfWeek = (double) time.getDayOfWeek().getValue() - 1;
        double hour = (double) time.getHour();
        double perimeter= getPerimeterByPid.get(id);

        int busy = model.predict(zid, month, dayOfMonth, dayOfWeek, hour, area,perimeter);
        return new PoiBusy(time, id, busy);
    }
}
