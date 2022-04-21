package com.wts.exam.mapper;

import com.wts.exam.domain.CardAnswerHis;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CardAnswerHisMapper {
    int deleteEntity(String id);

    int insertEntity(CardAnswerHis record);

    int insertSelective(CardAnswerHis record);

    CardAnswerHis getEntity(String id);

    int editEntity(CardAnswerHis record);

    int updateByPrimaryKey(CardAnswerHis record);

    int countEntitys(String cardId);

    int deleteByCardId(String cardId);


}