package com.farm.doc.mapper;

import com.farm.doc.domain.Docfile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DocfileMapper {
    int deleteEntity(String id);

    int insertEntity(Docfile record);

    int insertSelective(Docfile record);

    Docfile getEntity(String id);

    int editEntity(Docfile record);

    int updateByPrimaryKey(Docfile record);

    List<Docfile> getDocFilesByDocId(String docid);

    List<Docfile> getDocFilesByDocTextId(String testid);

    List<Docfile> findByAppid(String appid);
}