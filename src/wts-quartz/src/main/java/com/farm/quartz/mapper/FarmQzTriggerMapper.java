package com.farm.quartz.mapper;

import com.farm.quartz.domain.FarmQzTrigger;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FarmQzTriggerMapper {
    int deleteEntity(String id);

    int insertEntity(FarmQzTrigger record);

    int insertSelective(FarmQzTrigger record);

    FarmQzTrigger getEntity(String id);

    int editEntity(FarmQzTrigger record);

    int updateByPrimaryKey(FarmQzTrigger record);
}