package com.farm.exam.service.impl;

import com.farm.authority.FarmAuthorityService;
import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.farm.exam.mapper.ExamPopMapper;
import com.farm.exam.mapper.RoomMapper;
import com.farm.exam.domain.ExamPop;
import com.farm.exam.domain.ExamType;
import com.farm.exam.domain.Room;
import com.farm.exam.service.ExamPopsServiceInter;
import com.farm.exam.service.ExamTypeServiceInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExamPopsServiceImpl implements ExamPopsServiceInter {

    @Autowired
    private ExamPopMapper examPopMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private ExamTypeServiceInter examTypeServiceImpl;

    @Override
    @Transactional
    public boolean isNotJudger(String roomId, LoginUser currentUser) {
        return !isJudger(roomId, currentUser);
    }

    @Override
    @Transactional
    public boolean isJudger(String roomId, LoginUser currentUser) {
        Set<String> typeids = examTypeServiceImpl.getUserPopTypeids(
                currentUser.getId(), "2");
        Room room = roomMapper.getEntity(roomId);
        if (room == null) {
            throw new RuntimeException("the room[" + roomId + "] is not exist!");
        }
        String typeid = room.getExamtypeid();
        return typeids.contains(typeid);
    }

    @Override
    @Transactional
    public boolean isManager(String roomId, LoginUser currentUser) {
        Set<String> typeids = examTypeServiceImpl.getUserPopTypeids(
                currentUser.getId(), "1");
        String typeid = roomMapper.getEntity(roomId).getExamtypeid();
        return typeids.contains(typeid);
    }

    @Override
    @Transactional
    public ExamPop insertExampopEntity(ExamPop entity, LoginUser user) {
        // TODO ??????????????????,???????????????????????????
        // entity.setCuser(user.getId());
        // entity.setCtime(TimeTool.getTimeDate14());
        // entity.setCusername(user.getName());
        // entity.setEuser(user.getId());
        // entity.setEusername(user.getName());
        // entity.setEtime(TimeTool.getTimeDate14());
        // entity.setPstate("1");
        examPopMapper.insertEntity(entity);
        return entity;
    }

    @Override
    @Transactional
    public ExamPop editExampopEntity(ExamPop entity, LoginUser user) {
        // TODO ??????????????????,???????????????????????????
        ExamPop entity2 = examPopMapper.getEntity(entity.getId());
        // entity2.setEuser(user.getId());
        // entity2.setEusername(user.getName());
        // entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setTypeid(entity.getTypeid());
        entity2.setUsername(entity.getUsername());
        entity2.setUserid(entity.getUserid());
        entity2.setFuntype(entity.getFuntype());
        entity2.setId(entity.getId());
        examPopMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteExampopEntity(String id, LoginUser user) {
        ExamPop pop = examPopMapper.getEntity(id);
        {
            List<String> ids = new ArrayList<>();
            ids.add(pop.getTypeid());
            List<String> typeIds = examTypeServiceImpl.getAllSubType(ids);
            for (String typeid : typeIds) {
                examPopMapper.deleteByTypeIdAndUserIdAndFuntype(typeid, pop.getUserid(), pop.getFuntype());

                List<ExamPop> pops = examPopMapper.findByTypeIdAndFunType(typeid, pop.getFuntype());

                if (pops.size() == 0) {
                    ExamType type = examTypeServiceImpl
                            .getExamtypeEntity(typeid);
                    // 1:????????????.2:????????????.3:????????????.4:????????????
                    if (pop.getFuntype().equals("1")) {
                        type.setMngpop("1");
                    }
                    if (pop.getFuntype().equals("2")) {
                        type.setAdjudgepop("3");
                    }
                    if (pop.getFuntype().equals("3")) {
                        type.setQuerypop("3");
                    }
                    if (pop.getFuntype().equals("4")) {
                        type.setSuperpop("3");
                    }
                    examTypeServiceImpl.editExamtypeEntity(type, user);
                }
            }
        }

    }

    @Override
    @Transactional
    public ExamPop getExampopEntity(String id) {
        // TODO ??????????????????,???????????????????????????
        if (id == null) {
            return null;
        }
        return examPopMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createExampopSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_EXAM_POP a left join WTS_EXAM_TYPE b on a.TYPEID=b.ID",
                        "a.FUNTYPE as FUNTYPE,a.USERNAME as USERNAME,b.NAME as TYPENAME ,a.ID as ID");
        return dbQuery;
    }

    @Override
    @Transactional
    public void insertPop(List<String> userIds, List<String> typeIds,
                          String functype, LoginUser currentUser) {
        typeIds = examTypeServiceImpl.getAllSubType(typeIds);
        for (String typeid : typeIds) {
            for (String userid : userIds) {
                ExamPop pop = new ExamPop();
                pop.setFuntype(functype);
                pop.setTypeid(typeid);
                pop.setUserid(userid);
                LoginUser user = FarmAuthorityService.getInstance()
                        .getUserById(userid);
                if (user != null) {
                    pop.setUsername(user.getName());
                    examPopMapper.deleteByTypeIdAndUserIdAndFuntype(typeid, userid, functype);
                    examPopMapper.insertEntity(pop);
                    ExamType type = examTypeServiceImpl
                            .getExamtypeEntity(typeid);
                    // 1:????????????.2:????????????.3:????????????.4:????????????
                    if (functype.equals("1")) {
                        type.setMngpop("2");
                    }
                    if (functype.equals("2")) {
                        type.setAdjudgepop("2");
                    }
                    if (functype.equals("3")) {
                        type.setQuerypop("2");
                    }
                    if (functype.equals("4")) {
                        type.setSuperpop("2");
                    }
                    examTypeServiceImpl.editExamtypeEntity(type, currentUser);
                }
            }
        }
    }

}
