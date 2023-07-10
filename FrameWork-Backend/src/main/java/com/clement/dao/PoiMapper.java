package com.clement.dao;

import com.clement.pojo.Poi;
import com.clement.pojo.PoiInfo;
import com.clement.pojo.TaxiZone;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PoiMapper {
    @Select("select * from poi_detail where id = #{id}")
    PoiInfo getPoiInfoById (@Param("id") String id);

    @Select("select * from poi")
    List<Poi> getAllPoi();

    @Select("select zid, circle_lng, circle_lat, radius, busy from taxi_zone")
    List<TaxiZone> getAllTaxiZone();
}
