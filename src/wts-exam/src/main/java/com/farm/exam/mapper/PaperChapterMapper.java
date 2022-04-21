package com.farm.exam.mapper;

import com.farm.exam.domain.PaperChapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PaperChapterMapper {
    int deleteEntity(String id);

    int insertEntity(PaperChapter record);

    int insertSelective(PaperChapter record);

    PaperChapter getEntity(String id);

    int editEntity(PaperChapter record);

    int updateByPrimaryKey(PaperChapter record);

    List<PaperChapter> findByParentId(String parentId);

    List<PaperChapter> findByParentIdAndPtypeAndName(@Param("parentId") String parentId, @Param("ptype") String ptype, @Param("name") String name);

}