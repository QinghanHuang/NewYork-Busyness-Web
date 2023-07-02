package com.clement.dao;

import com.clement.pojo.Poi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PoiMapper {
    @Select("select * from poi where pid = #{pid}")
    Poi getPoiById (@Param("pid") int pid);

    @Select("select * from poi")
    List<Poi> getAllPoi ();
}
