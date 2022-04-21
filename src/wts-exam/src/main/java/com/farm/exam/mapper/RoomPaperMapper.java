package com.farm.exam.mapper;

import com.farm.exam.domain.RoomPaper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoomPaperMapper {
    int deleteEntity(String id);

    int insertEntity(RoomPaper record);

    int insertSelective(RoomPaper record);

    RoomPaper getEntity(String id);

    int editEntity(RoomPaper record);

    int updateByPrimaryKey(RoomPaper record);

    List<RoomPaper> findByRoomId(String roomid);

    List<RoomPaper> findByPaperIdAndRoomId(@Param("paperId") String paperId, @Param("roomid") String roomid);

    int deleteByRoomIdAndPaperId(@Param("roomId") String roomId, @Param("paperId") String paperId);

    int deleteByRoomId(String roomId);

    List<String> getPaperAliasByCardId(String id);

}