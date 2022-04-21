package com.wts.exam.mapper;

import com.wts.exam.domain.Room;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoomMapper {
    int deleteEntity(String id);

    int insertEntity(Room record);

    int insertSelective(Room record);

    Room getEntity(String id);

    int editEntity(Room record);

    int updateByPrimaryKey(Room record);

    List<Room> findByExamtypeid(String examtypeid);
}