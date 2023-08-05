package com.clement.dao;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.TaxiZone;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PoiMapper {
    @Select("SELECT id, name, detailedLocation, open, close, url1, url2, url3, introduction " +
            "FROM poi_detail " +
            "WHERE id = #{id}")
    PoiInfo getPoiInfoById (@Param("id") int id);

    @Select("SELECT id, name, lng, lat,photo FROM poi")
    List<Poi> getAllPois();

//    @Select("SELECT id FROM poi")
//    List<Integer> getPids();

    @Select("SELECT location_id, circle_lng, circle_lat, radius FROM taxi_zone")
    List<TaxiZone> getAllTaxiZones();

//    @Update("UPDATE taxi_zone SET busy=#{busy}, timestamp=#{timestamp} WHERE location_id=#{location_id}")
//    int updateBusy(@Param("location_id") int location_id, @Param("timestamp") String timestamp, @Param("busy") int busy);

    @Select("SELECT location_id FROM taxi_zone")
    List<Integer> getLocationIds();

    @Select("SELECT id FROM poi")
    List<Integer> getIds();

    @Select("SELECT location_id FROM poi WHERE id=#{id}")
    int getZidById(@Param("id") int id);
    @Select("SELECT area FROM poi WHERE id=#{id}")
    double getAreaByPid(@Param("id") int id);

    @Select("SELECT perimeter FROM poi WHERE id=#{id}")
    double getPerimeterByPid(@Param("id") int id);



//    @Select("SELECT timestamp FROM taxi_zone WHERE location_id=#{location_id}")
//    String getTimeStamp(@Param("location_id") int location_id);
}
