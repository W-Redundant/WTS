package com.farm.exam.mapper;

import com.farm.exam.domain.CardAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CardAnswerMapper {
    int deleteEntity(String id);

    int insertEntity(CardAnswer record);

    int insertSelective(CardAnswer record);

    CardAnswer getEntity(String id);

    int editEntity(CardAnswer record);

    int updateByPrimaryKey(CardAnswer record);

    List<CardAnswer> findByCardId(String cardId);

    int deleteByCardId(String cardId);

    int deleteByCardIdAndVersionIdAndAnswerId(@Param("CARDID")String cardId,@Param("VERSIONID")String versionId,@Param("ANSWERID")String anserId);

    List<CardAnswer> findByCardIdAndVersionId(@Param("CARDID")String cardId,@Param("VERSIONID")String versionId);
}