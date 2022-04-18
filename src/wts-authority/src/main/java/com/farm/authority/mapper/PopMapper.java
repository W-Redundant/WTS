package com.farm.authority.mapper;

import com.farm.authority.domain.Pop;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PopMapper {
    int deleteByPrimaryKey(String id);

    int insert(Pop record);

    int insertSelective(Pop record);

    Pop selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Pop record);

    int updateByPrimaryKey(Pop record);
}