package com.farm.authority.mapper;

import com.farm.authority.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insertEntity(User record);

    int insertSelective(User record);

    User getEntity(String id);

    int editEntity(User record);

    int updateByPrimaryKey(User record);

    List<User> findUserByLoginName(String loginname);

    List<User> findUserByLoginNameAndId(@Param("loginname")String loginname, @Param("id")String userId);

    int getUsersNum();

}