package com.wts.exam.mapper;

import com.wts.exam.domain.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaperMapper {
    int deleteEntity(String id);

    int insertEntity(Paper record);

    int insertSelective(Paper record);

    Paper getEntity(String id);

    int editEntity(Paper record);

    int updateByPrimaryKey(Paper record);
}