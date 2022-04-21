package com.farm.exam.mapper;

import com.farm.exam.domain.RandomStep;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RandomStepMapper {
    int deleteEntity(String id);

    int insertEntity(RandomStep record);

    int insertSelective(RandomStep record);

    RandomStep getEntity(String id);

    int editEntity(RandomStep record);

    int updateByPrimaryKey(RandomStep record);

    List<RandomStep> findByItemId(String itemId);
}