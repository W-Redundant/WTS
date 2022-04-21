package com.farm.exam.mapper;

import com.farm.exam.domain.CardHis;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CardHisMapper {
    int deleteEntity(String id);

    int insertEntity(CardHis record);

    int insertSelective(CardHis record);

    CardHis getEntity(String id);

    int editEntity(CardHis record);

    int updateByPrimaryKey(CardHis record);
}