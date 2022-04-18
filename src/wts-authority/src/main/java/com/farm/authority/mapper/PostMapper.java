package com.farm.authority.mapper;

import com.farm.authority.domain.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
    int deleteByPrimaryKey(String id);

    int insert(Post record);

    int insertSelective(Post record);

    Post selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Post record);

    int updateByPrimaryKey(Post record);
}