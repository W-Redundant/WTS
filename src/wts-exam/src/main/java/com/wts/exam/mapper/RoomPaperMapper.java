package com.wts.exam.mapper;

import com.wts.exam.domain.RoomPaper;
import org.apache.ibatis.annotations.Mapper;
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

}