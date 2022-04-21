package com.farm.quartz.mapper;

import com.farm.quartz.domain.FarmQzScheduler;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FarmQzSchedulerMapper {
    int deleteEntity(String id);

    int insertEntity(FarmQzScheduler record);

    int insertSelective(FarmQzScheduler record);

    FarmQzScheduler getEntity(String id);

    int editEntity(FarmQzScheduler record);

    int updateByPrimaryKey(FarmQzScheduler record);
}