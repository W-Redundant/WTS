package com.wts.exam.mapper;

import com.wts.exam.domain.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectMapper {
    int deleteEntity(String id);

    int insertEntity(Subject record);

    int insertSelective(Subject record);

    Subject getEntity(String id);

    int editEntity(Subject record);

    int updateByPrimaryKey(Subject record);
}