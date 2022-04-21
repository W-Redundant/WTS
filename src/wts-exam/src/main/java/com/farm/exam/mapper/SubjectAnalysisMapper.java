package com.farm.exam.mapper;

import com.farm.exam.domain.SubjectAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SubjectAnalysisMapper {
    int deleteEntity(String id);

    int deleteBySubjectId(String subjectid);


    int insertEntity(SubjectAnalysis record);

    int insertSelective(SubjectAnalysis record);

    SubjectAnalysis getEntity(String id);

    int editEntity(SubjectAnalysis record);

    int updateByPrimaryKey(SubjectAnalysis record);

    List<SubjectAnalysis> findBySubjectId(String subjectid);
}