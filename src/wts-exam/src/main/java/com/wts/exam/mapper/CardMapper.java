package com.wts.exam.mapper;

import com.wts.exam.domain.Card;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CardMapper {
    int deleteEntity(String id);

    int insertEntity(Card record);

    int insertSelective(Card record);

    Card getEntity(String id);

    int editEntity(Card record);

    int updateByPrimaryKey(Card record);

    List<Card> findByRoomId(String roomId);

    List<Card> findByPaperIdAndUserIdAndRoomId(@Param("PAPERID")String paperId, @Param("USERID")String userId, @Param("ROOMID")String roomId);

    List<Card> findByPaperIdAndRoomId(@Param("PAPERID")String paperId, @Param("ROOMID")String roomId);

    List<Card> findByRoomIdAndUserId(@Param("ROOMID")String roomId, @Param("USERID")String userId);

    int countByPaperIdAndRoomId(@Param("PAPERID")String paperId,@Param("ROOMID")String roomId);

    int countByPaperIdAndRoomIdAndPstate(@Param("PAPERID")String paperId, @Param("ROOMID")String roomId, @Param("PSTATE")String pstate);
}