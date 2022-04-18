package com.farm.authority.mapper;

import com.farm.authority.domain.Outuser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutuserMapper {
    int deleteByPrimaryKey(String id);

    int insert(Outuser record);

    int insertSelective(Outuser record);

    Outuser selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Outuser record);

    int updateByPrimaryKey(Outuser record);
}