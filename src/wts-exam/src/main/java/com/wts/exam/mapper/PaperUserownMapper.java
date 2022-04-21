package com.wts.exam.mapper;

import com.wts.exam.domain.PaperUserown;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaperUserownMapper {
    int deleteEntity(String id);

    int insertEntity(PaperUserown record);

    int insertSelective(PaperUserown record);

    PaperUserown getEntity(String id);

    int editEntity(PaperUserown record);

    int updateByPrimaryKey(PaperUserown record);

    int deleteByCardId(String cardId);
}