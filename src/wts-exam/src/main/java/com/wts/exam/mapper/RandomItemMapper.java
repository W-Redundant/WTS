package com.wts.exam.mapper;

import com.wts.exam.domain.RandomItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RandomItemMapper {
    int deleteEntity(String id);

    int insertEntity(RandomItem record);

    int insertSelective(RandomItem record);

    RandomItem getEntity(String id);

    int editEntity(RandomItem record);

    int updateByPrimaryKey(RandomItem record);
}