package com.farm.exam.mapper;

import com.farm.exam.domain.Material;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MaterialMapper {
    int deleteEntity(String id);

    int insertEntity(Material record);

    int insertSelective(Material record);

    Material getEntity(String id);

    int editEntity(Material record);

    int updateByPrimaryKey(Material record);
}