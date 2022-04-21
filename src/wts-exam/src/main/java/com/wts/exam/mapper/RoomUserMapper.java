package com.wts.exam.mapper;

import com.wts.exam.domain.RoomUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoomUserMapper {
    int deleteEntity(String id);

    int deleteByRoomId(String roomId);

    int insertEntity(RoomUser record);

    int insertSelective(RoomUser record);

    RoomUser getEntity(String id);

    int editEntity(RoomUser record);

    int updateByPrimaryKey(RoomUser record);

    int countByRoomId(String roomId);

    List<RoomUser> findByRoomIdAndUserId(@Param("roomId") String roomId, @Param("userId") String userId);

    int deleteByRoomIDAndUserId(@Param("roomId") String roomId, @Param("userId") String userId);
}