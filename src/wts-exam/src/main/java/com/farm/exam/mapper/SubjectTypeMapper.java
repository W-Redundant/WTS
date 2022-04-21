package com.farm.exam.mapper;

import com.farm.exam.domain.SubjectType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectTypeMapper {
    int deleteEntity(String id);

    int insertEntity(SubjectType record);

    int insertSelective(SubjectType record);

    SubjectType getEntity(String id);

    int editEntity(SubjectType record);

    int updateByPrimaryKey(SubjectType record);
}