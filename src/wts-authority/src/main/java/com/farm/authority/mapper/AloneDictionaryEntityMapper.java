package com.farm.authority.mapper;

import com.farm.authority.domain.AloneDictionaryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AloneDictionaryEntityMapper {
    int deleteByPrimaryKey(String id);

    int insert(AloneDictionaryEntity record);

    int insertSelective(AloneDictionaryEntity record);

    AloneDictionaryEntity selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AloneDictionaryEntity record);

    int updateByPrimaryKey(AloneDictionaryEntity record);
}