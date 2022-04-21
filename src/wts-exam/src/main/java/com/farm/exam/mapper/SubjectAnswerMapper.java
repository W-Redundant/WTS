package com.farm.exam.mapper;

import com.farm.exam.domain.SubjectAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectAnswerMapper {
    int deleteEntity(String id);

    int insertEntity(SubjectAnswer record);

    int insertSelective(SubjectAnswer record);

    SubjectAnswer getEntity(String id);

    int editEntity(SubjectAnswer record);

    int updateByPrimaryKey(SubjectAnswer record);
}