package com.clement.service;

import com.clement.pojo.prediction.PoiBusy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PredictionService {

    //List<ZoneBusy> getZoneBusys(LocalDate date);

    List<PoiBusy> getPoiBusys(LocalDate date);

    PoiBusy getPoiBusyById(int id, LocalDateTime time);

}
