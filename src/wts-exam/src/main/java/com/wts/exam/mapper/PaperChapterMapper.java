package com.wts.exam.mapper;

import com.wts.exam.domain.PaperChapter;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PaperChapterMapper {
    int deleteEntity(String id);

    int insertEntity(PaperChapter record);

    int insertSelective(PaperChapter record);

    PaperChapter getEntity(String id);

    int editEntity(PaperChapter record);

    int updateByPrimaryKey(PaperChapter record);
}