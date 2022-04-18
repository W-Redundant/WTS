package com.farm.authority.mapper;

import com.farm.authority.domain.AloneDictionaryType;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AloneDictionaryTypeMapper {
    int deleteByPrimaryKey(String id);

    int insert(AloneDictionaryType record);

    int insertSelective(AloneDictionaryType record);

    AloneDictionaryType selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AloneDictionaryType record);

    int updateByPrimaryKey(AloneDictionaryType record);
}