package com.farm.authority.mapper;

import com.farm.authority.domain.Outuser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OutuserMapper {
    int deleteEntity(String id);

    int insertEntity(Outuser record);

    int insertSelective(Outuser record);

    Outuser getEntity(String id);

    int editEntity(Outuser record);

    int updateByPrimaryKey(Outuser record);
}