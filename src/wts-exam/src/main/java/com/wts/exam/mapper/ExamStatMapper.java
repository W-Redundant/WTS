package com.wts.exam.mapper;

import com.wts.exam.domain.ExamStat;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ExamStatMapper {
    int deleteEntity(String id);

    int insertEntity(ExamStat record);

    int insertSelective(ExamStat record);

    ExamStat getEntity(String id);

    int editEntity(ExamStat record);

    int updateByPrimaryKey(ExamStat record);
}