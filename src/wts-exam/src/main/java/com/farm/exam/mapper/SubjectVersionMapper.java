package com.farm.exam.mapper;

import com.farm.exam.domain.SubjectVersion;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectVersionMapper {
    int deleteEntity(String id);

    int insertEntity(SubjectVersion record);

    int insertSelective(SubjectVersion record);

    SubjectVersion getEntity(String id);

    int editEntity(SubjectVersion record);

    int updateByPrimaryKey(SubjectVersion record);
}