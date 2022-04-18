package com.farm.authority.mapper;

import com.farm.authority.domain.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrganizationMapper {
    int deleteByPrimaryKey(String id);

    int insert(Organization record);

    int insertSelective(Organization record);

    Organization selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Organization record);

    int updateByPrimaryKey(Organization record);
}