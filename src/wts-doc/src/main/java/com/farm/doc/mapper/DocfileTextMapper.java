package com.farm.doc.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.farm.doc.domain.DocFileText;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DocfileTextMapper {
    int deleteByPrimaryKey(String id);

    int insertEntity(DocFileText record);

    int insertSelective(DocFileText record);

    DocFileText selectByPrimaryKey(String id);

    int editEntity(DocFileText record);

    int updateByPrimaryKey(DocFileText record);

    int deleteFileTextByFileid(String fileId);

    List<DocFileText> findByFileIdAndDocId(@Param("fileId")String fileId,@Param("docId")String docId);

    List<DocFileText> findByFileId(String fileId);
}