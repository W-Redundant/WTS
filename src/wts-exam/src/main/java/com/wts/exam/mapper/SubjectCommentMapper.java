package com.wts.exam.mapper;

import com.wts.exam.domain.SubjectComment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SubjectCommentMapper {
    int deleteEntity(String id);

    int insertEntity(SubjectComment record);

    int insertSelective(SubjectComment record);

    SubjectComment getEntity(String id);

    int editEntity(SubjectComment record);

    int updateByPrimaryKey(SubjectComment record);
}