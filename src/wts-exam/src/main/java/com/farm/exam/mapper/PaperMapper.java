package com.farm.exam.mapper;

import com.farm.exam.domain.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PaperMapper {
    int deleteEntity(String id);

    int insertEntity(Paper record);

    int insertSelective(Paper record);

    Paper getEntity(String id);

    int editEntity(Paper record);

    int updateByPrimaryKey(Paper record);

    List<Paper> findByExamtypeid(String examtypeid);
}