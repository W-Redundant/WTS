package com.wts.exam.mapper;

import com.wts.exam.domain.SubjectUserown;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectUserownMapper {
    int deleteEntity(String id);

    int insertEntity(SubjectUserown record);

    int insertSelective(SubjectUserown record);

    SubjectUserown getEntity(String id);

    int editEntity(SubjectUserown record);

    int updateByPrimaryKey(SubjectUserown record);

    int deleteBySubjectId(String subjectId);

}