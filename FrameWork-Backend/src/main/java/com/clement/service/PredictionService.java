package com.clement.service;

import java.util.HashMap;
import java.util.List;

public interface PredictionService {
    int getBusyById(int id, int hour);

    //TODO: param is date
    List<HashMap<Integer, Integer>> getZoneBusys();

    //TODO: param is date
    List<HashMap<Integer, Integer>> getPoiBusys();

}
