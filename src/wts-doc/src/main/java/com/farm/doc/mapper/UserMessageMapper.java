package com.farm.doc.mapper;

import com.farm.doc.domain.UserMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMessageMapper {
    int deleteEntity(String id);

    int insertEntity(UserMessage record);

    int insertSelective(UserMessage record);

    UserMessage getEntity(String id);

    int editEntity(UserMessage record);

    int updateByPrimaryKey(UserMessage record);
}