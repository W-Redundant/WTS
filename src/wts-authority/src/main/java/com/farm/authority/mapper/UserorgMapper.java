package com.farm.authority.mapper;

import com.farm.authority.domain.Userorg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserorgMapper {
    int deleteByPrimaryKey(String id);

    int insert(Userorg record);

    int insertSelective(Userorg record);

    Userorg selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Userorg record);

    int updateByPrimaryKey(Userorg record);
}