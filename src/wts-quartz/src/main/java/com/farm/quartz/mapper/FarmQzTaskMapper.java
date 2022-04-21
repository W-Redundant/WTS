package com.farm.quartz.mapper;

import com.farm.quartz.domain.FarmQzTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FarmQzTaskMapper {
    int deleteEntity(String id);

    int insertEntity(FarmQzTask record);

    int insertSelective(FarmQzTask record);

    FarmQzTask getEntity(String id);

    int editEntity(FarmQzTask record);

    int updateByPrimaryKey(FarmQzTask record);
}