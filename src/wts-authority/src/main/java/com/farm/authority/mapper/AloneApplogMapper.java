package com.farm.authority.mapper;

import com.farm.authority.domain.AloneApplog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AloneApplogMapper {
    int deleteByPrimaryKey(String id);

    int insert(AloneApplog record);

    int insertSelective(AloneApplog record);

    AloneApplog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AloneApplog record);

    int updateByPrimaryKey(AloneApplog record);
}