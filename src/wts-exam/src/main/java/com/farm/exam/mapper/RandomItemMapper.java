package com.farm.exam.mapper;

import com.farm.exam.domain.RandomItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RandomItemMapper {
    int deleteEntity(String id);

    int insertEntity(RandomItem record);

    int insertSelective(RandomItem record);

    RandomItem getEntity(String id);

    List<RandomItem> findByPstate(String pstate);

    int editEntity(RandomItem record);

    int updateByPrimaryKey(RandomItem record);
}