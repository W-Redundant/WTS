package com.wts.exam.mapper;

import com.wts.exam.domain.RoomUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RoomUserMapper {
    int deleteEntity(String id);

    int insertEntity(RoomUser record);

    int insertSelective(RoomUser record);

    RoomUser getEntity(String id);

    int editEntity(RoomUser record);

    int updateByPrimaryKey(RoomUser record);

    int countByRoomId(String roomId);
}