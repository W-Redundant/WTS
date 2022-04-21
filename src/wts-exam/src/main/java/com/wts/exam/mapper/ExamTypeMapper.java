package com.wts.exam.mapper;

import com.wts.exam.domain.ExamType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ExamTypeMapper {
    int deleteEntity(String id);

    int insertEntity(ExamType record);

    int insertSelective(ExamType record);

    ExamType getEntity(String id);

    int editEntity(ExamType record);

    int updateByPrimaryKey(ExamType record);
}