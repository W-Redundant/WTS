package com.farm.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DBSort;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import com.farm.exam.mapper.ExamPopMapper;
import com.farm.exam.mapper.ExamTypeMapper;
import com.farm.exam.mapper.PaperMapper;
import com.farm.exam.mapper.RoomMapper;
import com.farm.web.easyui.EasyUiTreeNode;
import com.farm.exam.dao.ExamTypeDaoInter;
import com.farm.exam.domain.ExamType;
import com.farm.exam.domain.Paper;
import com.farm.exam.domain.Room;
import com.farm.exam.domain.ex.ExamTypeUnit;
import com.farm.exam.domain.ex.RoomUnit;
import com.farm.exam.service.ExamTypeServiceInter;
import com.farm.exam.service.RoomServiceInter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;

@Service
public class ExamTypeServiceImpl implements ExamTypeServiceInter {
    @Autowired
    private ExamTypeDaoInter examtypeDaoImpl;
    @Autowired
    private ExamTypeMapper examTypeMapper;
    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private ExamPopMapper examPopMapper;
    @Autowired
    private RoomServiceInter roomServiceImpl;

    @Override
    @Transactional
    public ExamType insertExamtypeEntity(ExamType entity, LoginUser user) {
        entity.setCuser(user.getId());
        entity.setCtime(TimeTool.getTimeDate14());
        entity.setMuser(user.getId());
        entity.setUtime(TimeTool.getTimeDate14());
        entity.setState("1");
        if (entity.getParentid() == null) {
            entity.setParentid("NONE");
        }
        entity.setTreecode("NONE");
        entity.setMngpop("1");
        entity.setAdjudgepop("3");
        entity.setQuerypop("3");
        entity.setSuperpop("3");
        examTypeMapper.insertEntity(entity);
        initTreeCode(entity.getId());
        return entity;
    }

    private void initTreeCode(String treeNodeId) {
        ExamType node = getExamtypeEntity(treeNodeId);
        if (node.getParentid().equals("NONE")) {
            node.setTreecode(node.getId());
        } else {
            node.setTreecode(examTypeMapper.getEntity(node.getParentid())
                    .getTreecode() + node.getId());
        }
        examTypeMapper.editEntity(node);
    }

    @Override
    @Transactional
    public ExamType editExamtypeEntity(ExamType entity, LoginUser user) {
        ExamType entity2 = examTypeMapper.getEntity(entity.getId());
        entity2.setMuser(user.getId());
        entity2.setSort(entity.getSort());
        entity2.setState(entity.getState());
        entity2.setUtime(TimeTool.getTimeDate14());
        entity2.setComments(entity.getComments());
        entity2.setName(entity.getName());
        entity2.setId(entity.getId());
        examTypeMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteExamtypeEntity(String id, LoginUser user) {
        {
            List<ExamType> types = examTypeMapper.findByParentId(id);
            if (types.size() > 0) {
                throw new RuntimeException("分类下还有分类，请先删除子分类！");
            }
        }
        {
            List<Paper> papers = paperMapper.findByExamtypeid(id);

            if (papers.size() > 0) {
                throw new RuntimeException("分类下还有答卷，请先删除或移动答卷！");
            }
        }
        {
            List<Room> rooms = roomMapper.findByExamtypeid(id);

            if (rooms.size() > 0) {
                throw new RuntimeException("分类下还有答题室，请先删除或移动答题室！");
            }
        }
        examPopMapper.deleteByTypeId(id);

        examTypeMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public ExamType getExamtypeEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return examTypeMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createExamtypeSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_EXAM_TYPE",
                        "ID,MUSER,PARENTID,SORT,CUSER,STATE,UTIME,TREECODE,COMMENTS,NAME,CTIME,MNGPOP,ADJUDGEPOP,QUERYPOP,SUPERPOP");
        return dbQuery;
    }

    @Override
    @Transactional
    public void moveTreeNode(String orgId, String targetOrgId,
                             LoginUser currentUser) {
        String[] orgIds = orgId.split(",");
        for (int i = 0; i < orgIds.length; i++) {
            // 移动节点
            ExamType node = getExamtypeEntity(orgIds[i]);
            // if (node.getParentid().equals("NONE")) {
            // throw new RuntimeException("不能够移动根节点!");
            // }
            if (targetOrgId.equals("NONE")) {
                node.setParentid("NONE");
            } else {
                ExamType target = getExamtypeEntity(targetOrgId);
                if (target.getTreecode().indexOf(node.getTreecode()) >= 0) {
                    throw new RuntimeException("不能够移动到其子节点下!");
                }
                node.setParentid(targetOrgId);
            }
            examTypeMapper.editEntity(node);
            // 构造所有树TREECODE
            String treeCode = examTypeMapper.getEntity(orgIds[i]).getTreecode();

            List<ExamType> list = examTypeMapper.getAllSubNodes(treeCode + "%");
            for (ExamType type : list) {
                initTreeCode(type.getId());
            }
        }
    }

    @Override
    @Transactional
    public List<ExamTypeUnit> getRootTypeUnits(LoginUser loginUser) {
        // 1:管理权限.2:判卷权限.//（暂缓实现--3:查询权限.4:超级权限）
        Set<String> adjudgeTypeIds = new HashSet<>();
        if (loginUser != null) {
            adjudgeTypeIds = getUserPopTypeids(loginUser.getId(), "2");
        }
        List<ExamTypeUnit> typeunits = new ArrayList<>();
        // 获得所有一级目录

        List<ExamType> types = examTypeMapper.findByParentIdAndState("NONE","1");

        Collections.sort(types, new Comparator<ExamType>() {
            @Override
            public int compare(ExamType o1, ExamType o2) {
                return o1.getSort() - o2.getSort();
            }
        });

        // 从一级目录下找到所有考场房间加载进来（本人有考试权限，本人有判卷权限，本人有管理权限）
        for (ExamType type : types) {
            ExamTypeUnit unit = new ExamTypeUnit();
            unit.setType(type);
            DataQuery query = DataQuery
                    .getInstance(
                            "1",
                            "b.EXAMTYPEID AS EXAMTYPEID,b.SSORTTYPE AS SSORTTYPE,b.PSTATE AS PSTATE,b.OSORTTYPE AS OSORTTYPE,b.PSHOWTYPE AS PSHOWTYPE,B.IMGID AS IMGID, b.TIMETYPE AS TIMETYPE, b.STARTTIME AS STARTTIME, b.ENDTIME AS ENDTIME, b.WRITETYPE AS WRITETYPE, b.ROOMNOTE AS ROOMNOTE, b.TIMELEN AS TIMELEN, b. NAME AS NAME, b.COUNTTYPE AS COUNTTYPE, B.ID AS ID",
                            "WTS_ROOM b left join WTS_EXAM_TYPE a on a.ID=b.EXAMTYPEID");
            query.addRule(new DBRule("a.TREECODE", type.getId(), "like"));
            // 发布和结束状态的答题室可以在前台显示
            query.addSqlRule(" and (PSTATE='2' or PSTATE='3' )");
            query.setPagesize(6);
            query.setNoCount();
            query.addSort(new DBSort("b.CTIME", "DESC"));
            try {
                List<Room> rooms = query.search().getObjectList(Room.class);
                List<RoomUnit> roomUnits = new ArrayList<>();
                for (Room room : rooms) {
                    RoomUnit roomunit = new RoomUnit();
                    roomunit.setCurrentUserAble(false);
                    roomunit.setCurrentAdjudgepopAble(false);
                    roomunit.setCurrentMngpopAble(false);
                    roomunit.setCurrentTimeAble(false);
                    roomunit.setRoom(room);
                    ExamType examtype = examTypeMapper.getEntity(room
                            .getExamtypeid());
                    roomunit.setType(examtype);
                    if (roomServiceImpl.isLiveTimeRoom(room)) {
                        roomunit.setCurrentTimeAble(true);
                    }
                    if (roomServiceImpl.isUserAbleRoom(room.getId(), loginUser)) {
                        roomunit.setCurrentUserAble(true);
                    }
                    if (adjudgeTypeIds.contains(roomunit.getRoom()
                            .getExamtypeid())) {
                        roomunit.setCurrentAdjudgepopAble(true);
                    }
                    if (roomunit.isCurrentAdjudgepopAble()
                            || roomunit.isCurrentMngpopAble()
                            || roomunit.isCurrentUserAble()) {
                        roomUnits.add(roomunit);
                    }
                }
                unit.setRooms(roomUnits);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (unit.getRooms().size() > 0) {
                typeunits.add(unit);
            }
        }
        return typeunits;

    }

    @Override
    @Transactional
    public List<String> getAllSubType(List<String> typeIds) {
        return examtypeDaoImpl.getAllSubType(typeIds);
    }

    @Override
    @Transactional
    public List<EasyUiTreeNode> RunPopFilter(List<EasyUiTreeNode> list,
                                             String funtype, LoginUser currentUser) {
        if (StringUtils.isBlank(funtype)) {
            throw new RuntimeException(" the var funtype is not exist!");
        }
        if (funtype.trim().equals("0")) {
            return list;
        }
        if (funtype.trim().equals("1")) {
            // 1使用权
            Set<String> typeids = getUserPopTypeids(currentUser.getId(),
                    funtype);
            list = deleteEasyUiTreeNodeByPop(list, typeids);
            return list;
        }
        if (funtype.trim().equals("2")) {
            // 2维护权
            Set<String> typeids = getUserPopTypeids(currentUser.getId(),
                    funtype);
            list = deleteEasyUiTreeNodeByPop(list, typeids);
            return list;
        }
        return list;
    }

    /**
     * 根据分类id过掉没有权限的分类
     *
     * @param list
     * @param typeids
     * @return
     */
    private List<EasyUiTreeNode> deleteEasyUiTreeNodeByPop(
            List<EasyUiTreeNode> list, Set<String> typeids) {
        for (Iterator<EasyUiTreeNode> ite = list.iterator(); ite.hasNext(); ) {
            EasyUiTreeNode node = ite.next();
            if (!node.getId().equals("NONE") && !typeids.contains(node.getId())) {
                ite.remove();
            } else {
                deleteEasyUiTreeNodeByPop(node.getChildren(), typeids);
            }
        }
        return list;
    }

    @Override
    @Transactional
    public Set<String> getUserPopTypeids(String userId, String... funtype) {
        return examtypeDaoImpl.getUserPopTypeids(userId, funtype);
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getTypePopUsers(String examtypeid,
                                                     String funtype) {
        return examtypeDaoImpl.getTypePopUsers(examtypeid, funtype);
    }
}
