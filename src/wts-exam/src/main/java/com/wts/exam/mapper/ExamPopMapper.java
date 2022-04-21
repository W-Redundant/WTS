package com.wts.exam.mapper;

import com.wts.exam.domain.ExamPop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ExamPopMapper {
    int deleteEntity(String id);

    int insertEntity(ExamPop record);

    int insertSelective(ExamPop record);

    ExamPop getEntity(String id);

    int editEntity(ExamPop record);

    int updateByPrimaryKey(ExamPop record);

    int deleteByTypeIdAndUserIdAndFuntype(@Param("TYPEID") String typeId, @Param("USERID") String userId, @Param("FUNTYPE") String funtype);

    List<ExamPop> findByTypeIdAndFunType(@Param("typeId") String typeId, @Param("funType") String funType);

    int deleteByTypeId(String typeId);
}