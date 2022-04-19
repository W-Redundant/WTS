package com.farm.authority.mapper;

import com.farm.authority.domain.Actiontree;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ActiontreeMapper {
    int deleteByPrimaryKey(String id);

    int insert(Actiontree record);

    int insertSelective(Actiontree record);

    Actiontree selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Actiontree record);

    int updateByPrimaryKey(Actiontree record);

    Actiontree selectByParentId(String parentId);

    List<Actiontree> getAllSubNodes(String treeCode);

    List<Actiontree> selectEntitys(String parentid);
}