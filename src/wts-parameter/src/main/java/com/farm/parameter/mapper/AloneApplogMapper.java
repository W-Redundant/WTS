package com.farm.parameter.mapper;

import com.farm.parameter.domain.AloneApplog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AloneApplogMapper {
    int deleteEntity(String id);

    int insertEntity(AloneApplog record);

    int insertSelective(AloneApplog record);

    AloneApplog getEntity(String id);

    int editEntity(AloneApplog record);

    int updateByPrimaryKey(AloneApplog record);
}