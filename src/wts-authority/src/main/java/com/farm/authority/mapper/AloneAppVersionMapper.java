package com.farm.authority.mapper;

import com.farm.authority.domain.AloneAppVersion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AloneAppVersionMapper {
    int deleteByPrimaryKey(String version);

    int insert(AloneAppVersion record);

    int insertSelective(AloneAppVersion record);

    AloneAppVersion selectByPrimaryKey(String version);

    int updateByPrimaryKeySelective(AloneAppVersion record);

    int updateByPrimaryKey(AloneAppVersion record);
}