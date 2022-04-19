package com.farm.authority.mapper;

import com.farm.authority.domain.Userpost;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserpostMapper {
    int deleteByPrimaryKey(String id);

    int insertEntity(Userpost record);

    int insertSelective(Userpost record);

    Userpost selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Userpost record);

    int updateByPrimaryKey(Userpost record);

    List<Userpost> getTempUserPost(String userId);
}