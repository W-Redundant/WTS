package com.farm.authority.mapper;

import com.farm.authority.domain.Userpost;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserpostMapper {
    int deleteByPrimaryKey(String id);

    int insert(Userpost record);

    int insertSelective(Userpost record);

    Userpost selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Userpost record);

    int updateByPrimaryKey(Userpost record);
}