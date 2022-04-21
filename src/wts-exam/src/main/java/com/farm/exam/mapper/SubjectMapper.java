package com.farm.exam.mapper;

import com.farm.exam.domain.Subject;
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

    int countByMaterialid(String materialid);
}