package com.clement.service.impl;

import com.clement.dao.PoiMapper;
import com.clement.pojo.prediction.PoiBusy;
import com.clement.pojo.prediction.ZoneBusy;
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
@Service
public class PredictionServiceImpl implements PredictionService {
    @Resource
    PoiMapper mapper;

    Random random = new Random();



    List<Integer> locationIds;
    List<Integer> pids;
    Map<Integer, Integer> map;


    @PostConstruct
    public void init() {
        locationIds = mapper.getLocationIds();
        pids = mapper.getIds();
        map = new HashMap<>();
        for (Integer pid : pids) {
            int zid = mapper.getZidById(pid);
            map.put(pid, zid);
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

        for (int hour = 0; hour < 24; hour++) {
            for (Integer pid : pids) {
                int busy = random.nextInt(5) + 1;
                LocalDateTime time = date.atTime(hour, 0);
                poiBusyList.add(new PoiBusy(time, pid, busy));
            }
        }
        return poiBusyList;
    }

    public PoiBusy getPoiBusyById(int id, LocalDateTime time){
        int busy = random.nextInt(5) + 1;
        return new PoiBusy(time,id,busy);
    }
}
