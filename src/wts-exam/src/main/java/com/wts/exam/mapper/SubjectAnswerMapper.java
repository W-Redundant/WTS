package com.wts.exam.mapper;

import com.wts.exam.domain.SubjectAnswer;
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