package com.farm.authority.mapper;

import com.farm.authority.domain.Userorg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserorgMapper {
    int deleteByPrimaryKey(String id);

    int deleteByUseridAndOrganizationid(@Param("USERID") String userid,@Param("ORGANIZATIONID") String organizationid);

    int deleteByUserId(String userId);

    int insertEntity(Userorg record);

    int insertSelective(Userorg record);

    Userorg selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Userorg record);

    int updateByPrimaryKey(Userorg record);
}