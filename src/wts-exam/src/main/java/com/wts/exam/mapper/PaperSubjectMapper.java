package com.wts.exam.mapper;

import com.wts.exam.domain.PaperSubject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PaperSubjectMapper {
    int deleteEntity(String id);

    int insertEntity(PaperSubject record);

    int insertSelective(PaperSubject record);

    PaperSubject getEntity(String id);

    int editEntity(PaperSubject record);

    int updateByPrimaryKey(PaperSubject record);

    List<PaperSubject> findByPaperId(String paperId);

    List<PaperSubject> findByChapterId(String chapterid);

    List<PaperSubject> findByPaperIdAndSubjectid(@Param("paperId") String paperId, @Param("subjectId") String subjectId);

    List<PaperSubject> findByChapteridAndPaperIdAndSubjectid(@Param("chapterid") String chapterid,@Param("paperId") String paperId, @Param("subjectId") String subjectId);

    int countByPaperId(String paperId);

    int countByChapterid(String chapterid);
}