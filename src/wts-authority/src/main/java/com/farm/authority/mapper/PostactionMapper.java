package com.farm.authority.mapper;

import com.farm.authority.domain.Postaction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PostactionMapper {
    int deleteByPrimaryKey(String id);

    int deleteByPostid(String postid);

    int insertEntity(Postaction record);

    int insertSelective(Postaction record);

    Postaction selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Postaction record);

    int updateByPrimaryKey(Postaction record);
}