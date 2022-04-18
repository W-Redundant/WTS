package com.farm.authority.mapper;

import com.farm.authority.domain.AloneParameter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AloneParameterMapper {
    int deleteByPrimaryKey(String id);

    int insert(AloneParameter record);

    int insertSelective(AloneParameter record);

    AloneParameter selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AloneParameter record);

    int updateByPrimaryKey(AloneParameter record);
}