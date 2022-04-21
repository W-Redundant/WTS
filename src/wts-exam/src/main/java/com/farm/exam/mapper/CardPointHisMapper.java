package com.farm.exam.mapper;

import com.farm.exam.domain.CardPointHis;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CardPointHisMapper {
    int deleteEntity(String id);

    int insertEntity(CardPointHis record);

    int insertSelective(CardPointHis record);

    CardPointHis getEntity(String id);

    int editEntity(CardPointHis record);

    int updateByPrimaryKey(CardPointHis record);

    int deleteByCardId(String cardId);

    int countEntitys(String cardId);

}