package com.wts.exam.mapper;

import com.wts.exam.domain.CardPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CardPointMapper {
    int deleteEntity(String id);

    int insert(CardPoint record);

    int insertEntity(CardPoint record);

    CardPoint getEntity(String id);

    int editEntity(CardPoint record);

    int updateByPrimaryKey(CardPoint record);

    List<CardPoint> findByCardId(String cardid);

    int deleteByCardId(String cardId);

    int deleteByCardIdAndVersionId(@Param("CARDID") String cardId, @Param("VERSIONID") String versionId);

}