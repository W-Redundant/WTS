package com.farm.parameter.mapper;

import com.farm.parameter.domain.AloneParameterLocal;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AloneParameterLocalMapper {
    int deleteEntity(String id);

    int insertEntity(AloneParameterLocal record);

    int insertSelective(AloneParameterLocal record);

    AloneParameterLocal getEntity(String id);

    int editEntity(AloneParameterLocal record);

    int updateByPrimaryKey(AloneParameterLocal record);
}