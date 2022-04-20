package com.farm.doc.mapper;

import com.farm.doc.domain.MessageSender;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MessageSenderMapper {
    int deleteByPrimaryKey(String id);

    int insertEntity(MessageSender record);

    int insertSelective(MessageSender record);

    MessageSender selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MessageSender record);

    int updateByPrimaryKey(MessageSender record);

    int deleteAll();

    int deleteByAppidAndModelid(@Param("appid") String appid, @Param("modelid") String modelid);

}