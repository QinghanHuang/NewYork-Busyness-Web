package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.prediction.PoiBusy;
import com.clement.pojo.prediction.ZoneBusy;
import com.clement.service.DemoPmmlModelService;
import com.clement.service.PredictionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    Random random = new Random();



    List<Integer> locationIds;
    List<Integer> pids;
    Map<Integer, Integer> getZid;
    Map<Integer, Double> getArea;


    @PostConstruct
    public void init() {
        locationIds = mapper.getLocationIds();
        pids = mapper.getIds();
        getZid = new HashMap<>();
        getArea=new HashMap<>();
        for (Integer pid : pids) {
            int zid = mapper.getZidById(pid);
            getZid.put(pid, zid);
            double area=mapper.getAreaByPid(pid);
            getArea.put(pid,area);
        }
    }

    public List<ZoneBusy> getZoneBusys(LocalDate date) {
        List<ZoneBusy> zoneBusyList = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            for (int locationId : locationIds) {
                int busy = random.nextInt(5) + 1;
                LocalDateTime time = date.atTime(hour, 0);
                zoneBusyList.add(new ZoneBusy(time, locationId, busy));
            }
        }
        return zoneBusyList;
    }

    public List<PoiBusy> getPoiBusys(LocalDate date) {
        List<PoiBusy> poiBusyList=new ArrayList<>();

        Double month=(double)date.getMonth().getValue();
        Double dayOfMonth=(double)date.getDayOfMonth();
        Double dayOfWeek = (double)date.getDayOfWeek().getValue()-1;


        for (int hour = 0; hour < 24; hour++) {
            for (Integer pid : pids) {
                Double zid= (double)getZid.get(pid);
                Double area= getArea.get(pid);

                int busy =model.predict(zid,month,dayOfMonth,dayOfWeek,(double)hour,area);
                LocalDateTime time = date.atTime(hour, 0);
                poiBusyList.add(new PoiBusy(time, pid, busy));
            }
        }
        return poiBusyList;
    }

    public PoiBusy getPoiBusyById(int id, LocalDateTime time){
        Double zid= (double)getZid.get(id);
        Double area= getArea.get(id);
        Double month=(double)time.getMonth().getValue();
        Double dayOfMonth=(double)time.getDayOfMonth();
        Double dayOfWeek = (double)time.getDayOfWeek().getValue()-1;
        Double hour = (double)time.getHour();

        int busy =model.predict(zid,month,dayOfMonth,dayOfWeek,hour,area);
        return new PoiBusy(time,id,busy);
    }
}
