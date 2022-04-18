package com.farm.authority.mapper;

import com.farm.authority.domain.Actiontree;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ActiontreeMapper {
    int deleteByPrimaryKey(String id);

    int insert(Actiontree record);

    int insertSelective(Actiontree record);

    Actiontree selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Actiontree record);

    int updateByPrimaryKey(Actiontree record);
}