package com.farm.authority.mapper;

import com.farm.authority.domain.AloneParameterLocal;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AloneParameterLocalMapper {
    int deleteByPrimaryKey(String id);

    int insert(AloneParameterLocal record);

    int insertSelective(AloneParameterLocal record);

    AloneParameterLocal selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(AloneParameterLocal record);

    int updateByPrimaryKey(AloneParameterLocal record);
}