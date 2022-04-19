package com.farm.authority.mapper;

import com.farm.authority.domain.Action;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ActionMapper {
    int deleteByPrimaryKey(String id);

    int insert(Action record);

    int insertSelective(Action record);

    Action selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Action record);

    int updateByPrimaryKey(Action record);

    List<Action> selectAll();

    Action getEntityByKey(String authkey);
}