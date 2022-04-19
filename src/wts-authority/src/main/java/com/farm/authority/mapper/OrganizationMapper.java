package com.farm.authority.mapper;

import com.farm.authority.domain.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrganizationMapper {
    int deleteEntity(String id);

    int insertEntity(Organization record);

    int insertSelective(Organization record);

    Organization getEntity(String id);

    int editEntity(Organization record);

    int updateByPrimaryKey(Organization record);

    List<Organization> getAllSubNodes(String treecode);

    List<Organization> getList();

    List<String> getAllOrgComments();
}