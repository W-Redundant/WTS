package com.farm.parameter.mapper;

import com.farm.parameter.domain.AloneParameter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AloneParameterMapper {
    int deleteEntity(String id);

    int insertEntity(AloneParameter record);

    int insertSelective(AloneParameter record);

    AloneParameter getEntity(String id);

    int editEntity(AloneParameter record);

    int updateByPrimaryKey(AloneParameter record);
}