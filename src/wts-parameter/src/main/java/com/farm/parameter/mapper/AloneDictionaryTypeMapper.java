package com.farm.parameter.mapper;

import com.farm.parameter.domain.AloneDictionaryType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AloneDictionaryTypeMapper {
    int deleteEntity(String id);

    int insertEntity(AloneDictionaryType record);

    int insertSelective(AloneDictionaryType record);

    AloneDictionaryType getEntity(String id);

    int editEntity(AloneDictionaryType record);

    int updateByPrimaryKey(AloneDictionaryType record);
}