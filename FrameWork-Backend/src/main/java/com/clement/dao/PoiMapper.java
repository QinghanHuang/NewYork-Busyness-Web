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
    @Select("select id, name, detailedLocation, open, close, url1, url2, url3, introduction from poi_detail where id = #{id}")
    PoiInfo getPoiInfoById (@Param("id") String id);

    @Select("select id, name, lng, lat from poi")
    List<Poi> getAllPoi();

    @Select("select zid, circle_lng, circle_lat, radius from taxi_zone")
    List<TaxiZone> getAllTaxiZone();
}
