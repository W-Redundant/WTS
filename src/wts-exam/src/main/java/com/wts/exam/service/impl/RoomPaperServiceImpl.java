package com.wts.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.wts.exam.domain.RoomPaper;
import com.wts.exam.mapper.RoomPaperMapper;
import com.wts.exam.service.RoomPaperServiceInter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomPaperServiceImpl implements RoomPaperServiceInter {

    @Autowired
    private RoomPaperMapper roomPaperMapper;

    @Override
    @Transactional
    public RoomPaper insertRoompaperEntity(RoomPaper entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        // entity.setCuser(user.getId());
        // entity.setCtime(TimeTool.getTimeDate14());
        // entity.setCusername(user.getName());
        // entity.setEuser(user.getId());
        // entity.setEusername(user.getName());
        // entity.setEtime(TimeTool.getTimeDate14());
        // entity.setPstate("1");
        roomPaperMapper.insertEntity(entity);
        return entity;
    }

    @Override
    @Transactional
    public RoomPaper editRoompaperEntity(RoomPaper entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        RoomPaper entity2 = roomPaperMapper.getEntity(entity.getId());
        // entity2.setEuser(user.getId());
        // entity2.setEusername(user.getName());
        // entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setPaperid(entity.getPaperid());
        entity2.setRoomid(entity.getRoomid());
        entity2.setId(entity.getId());
        roomPaperMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteRoompaperEntity(String id, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        roomPaperMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public RoomPaper getRoompaperEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return roomPaperMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createRoompaperSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_ROOM_PAPER A LEFT JOIN WTS_PAPER B ON A.PAPERID=B.ID",
                        "A.ID AS ID,A.PAPERID AS PAPERID,A.NAME as NAME,A.ROOMID AS ROOMID,B.NAME AS PAPERNAME,B.PSTATE AS PAPERSTATE,B.CTIME as PAPERTIME");
        return dbQuery;
    }

    @Override
    @Transactional
    public void addRoomPaper(String roomid, String paperid,
                             LoginUser currentUser) {
        // 先删除
        roomPaperMapper.deleteByRoomIdAndPaperId(roomid, paperid);
        // 在添加
        RoomPaper entity = new RoomPaper();
        entity.setPaperid(paperid);
        entity.setRoomid(roomid);
        roomPaperMapper.insertEntity(entity);
    }

    @Override
    @Transactional
    public void editOtherName(String roompaperid, String name,
                              LoginUser currentUser) {
        RoomPaper entity = getRoompaperEntity(roompaperid);
        entity.setName(name);
        roomPaperMapper.editEntity(entity);
    }

    @Override
    @Transactional
    public void clearOtherName(String roompaperid, LoginUser currentUser) {
        RoomPaper entity = getRoompaperEntity(roompaperid);
        entity.setName(null);
        roomPaperMapper.editEntity(entity);
    }

    @Override
    @Transactional
    public String getPaperAlias(String roomid, String paperId) {
        if (StringUtils.isBlank(roomid) || StringUtils.isBlank(paperId)) {
            return null;
        }
        List<RoomPaper> rpList = roomPaperMapper.findByPaperIdAndRoomId(paperId, roomid);

        if (rpList.size() >= 0) {
            return rpList.get(0).getName();
        }
        return null;
    }

    @Override
    @Transactional
    public String getPaperAlias(String cardId) {
        List<String> names = roomPaperMapper.getPaperAliasByCardId(cardId);
        if (names.size() > 0) {
            return names.get(0);
        }
        return null;
    }
}
