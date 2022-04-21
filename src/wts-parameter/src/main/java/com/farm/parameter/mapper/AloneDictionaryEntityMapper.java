package com.farm.parameter.mapper;

import com.farm.parameter.domain.AloneDictionaryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AloneDictionaryEntityMapper {
    int deleteEntity(String id);

    int insertEntity(AloneDictionaryEntity record);

    int insertSelective(AloneDictionaryEntity record);

    AloneDictionaryEntity getEntity(String id);

    int editEntity(AloneDictionaryEntity record);

    int updateByPrimaryKey(AloneDictionaryEntity record);
}