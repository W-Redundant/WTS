package com.farm.authority.mapper;

import com.farm.authority.domain.Postaction;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostactionMapper {
    int deleteByPrimaryKey(String id);

    int insert(Postaction record);

    int insertSelective(Postaction record);

    Postaction selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Postaction record);

    int updateByPrimaryKey(Postaction record);
}