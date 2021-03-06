package com.farm.exam.mapper;

import com.farm.exam.domain.ExamType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@MapperScan
public interface ExamTypeMapper {
    int deleteEntity(String id);

    int insertEntity(ExamType record);

    int insertSelective(ExamType record);

    ExamType getEntity(String id);

    int editEntity(ExamType record);

    int updateByPrimaryKey(ExamType record);

    List<ExamType> findByParentId(String parentId);

    List<ExamType> getAllSubNodes(String treeCode);

    List<ExamType> findByParentIdAndState(@Param("parentId") String parentId, @Param("state") String state);




}