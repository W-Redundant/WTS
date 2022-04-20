package com.farm.doc.mapper;

import com.farm.doc.domain.MessageModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MessageModelMapper {
    int deleteEntity(String id);

    int insertEntity(MessageModel record);

    int insertSelective(MessageModel record);

    MessageModel getEntity(String id);

    int editEntity(MessageModel record);

    int updateByPrimaryKey(MessageModel record);

    int deleteAll();
}