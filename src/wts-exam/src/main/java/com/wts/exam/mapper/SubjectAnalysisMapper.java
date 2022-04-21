package com.wts.exam.mapper;

import com.wts.exam.domain.SubjectAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectAnalysisMapper {
    int deleteEntity(String id);

    int insertEntity(SubjectAnalysis record);

    int insertSelective(SubjectAnalysis record);

    SubjectAnalysis getEntity(String id);

    int editEntity(SubjectAnalysis record);

    int updateByPrimaryKey(SubjectAnalysis record);
}