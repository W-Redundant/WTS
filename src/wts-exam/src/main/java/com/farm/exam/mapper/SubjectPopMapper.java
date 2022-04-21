package com.farm.exam.mapper;

import com.farm.exam.domain.SubjectPop;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectPopMapper {
    int deleteEntity(String id);

    int insertEntity(SubjectPop record);

    int insertSelective(SubjectPop record);

    SubjectPop getEntity(String id);

    int editEntity(SubjectPop record);

    int updateByPrimaryKey(SubjectPop record);
}