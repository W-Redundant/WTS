package com.farm.authority.service.impl;

import com.farm.authority.FarmAuthorityService;
import com.farm.authority.domain.Organization;
import com.farm.authority.domain.Pop;
import com.farm.authority.domain.Post;
import com.farm.authority.mapper.PopMapper;
import com.farm.authority.service.OrganizationServiceInter;
import com.farm.authority.service.PopServiceInter;
import com.farm.authority.service.UserServiceInter;
import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import com.farm.web.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class PopServiceImpl implements PopServiceInter {

    @Autowired
    private OrganizationServiceInter organizationServiceImpl;

    @Autowired
    private UserServiceInter userServiceImpl;

    @Autowired
    private PopMapper popMapper;

    @Override
    @Transactional
    public Pop getPopEntity(String id) {
        if (id == null) {
            return null;
        }
        return popMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createPopSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery.init(query, "ALONE_AUTH_POP",
                "PCONTENT,CUSERNAME,PSTATE,CUSER,CTIME,POPTYPE,OID,ONAME,TARGETTYPE,TARGETID,TARGETNAME,ID");
        return dbQuery;
    }

    @Override
    @Transactional
    public void addUserPop(String targetId, String targetName, String oid, String targetType, LoginUser user) {
        Pop pop = new Pop();
        pop.setCtime(TimeTool.getTimeDate14());
        pop.setCuser(user.getId());
        pop.setCusername(user.getName());
        pop.setOid(oid);
        pop.setOname(FarmAuthorityService.getInstance().getUserById(oid).getName());
        // pop.setPcontent(pcontent);
        pop.setPoptype("1");
        pop.setPstate("1");
        pop.setTargetid(targetId);
        pop.setTargetname(targetName);
        pop.setTargettype(targetType);
        popMapper.deleteEntityByTargetidAndOid(targetId, oid, targetType);
        popMapper.insertEntity(pop);
    }

    @Override
    @Transactional
    public void addOrgPop(String targetId, String targetName, String oid, String targetType, LoginUser user) {
        Pop pop = new Pop();
        pop.setCtime(TimeTool.getTimeDate14());
        pop.setCuser(user.getId());
        pop.setCusername(user.getName());
        pop.setOid(oid);
        pop.setOname(organizationServiceImpl.getOrganizationEntity(oid).getName());
        // pop.setPcontent(pcontent);
        pop.setPoptype("2");
        pop.setPstate("1");
        pop.setTargetid(targetId);
        pop.setTargetname(targetName);
        pop.setTargettype(targetType);
        popMapper.deleteEntityByTargetidAndOid(targetId, oid, targetType);
        popMapper.insertEntity(pop);

    }

    @Override
    @Transactional
    public void addPostPop(String targetId, String targetName, String oid, String targetType, LoginUser user) {
        Pop pop = new Pop();
        pop.setCtime(TimeTool.getTimeDate14());
        pop.setCuser(user.getId());
        pop.setCusername(user.getName());
        pop.setOid(oid);
        pop.setOname(organizationServiceImpl.getPostEntity(oid).getName());
        // pop.setPcontent(pcontent);
        pop.setPoptype("3");
        pop.setPstate("1");
        pop.setTargetid(targetId);
        pop.setTargetname(targetName);
        pop.setTargettype(targetType);
        popMapper.deleteEntityByTargetidAndOid(targetId, oid, targetType);
        popMapper.insertEntity(pop);

    }

    @Override
    @Transactional
    public void delPop(String popId, LoginUser user) {
        Pop pop = popMapper.getEntity(popId);
        popMapper.deleteEntity(pop.getId());
    }

    @Override
    @Transactional
    public boolean isUserHaveTargetid(HttpSession session, String targetId, boolean defaultAble) {
        String USER_TARGETPOP = "USER_TARGETPOP";
        // ??????????????????????????????
        boolean isInitPopSesion;
        LoginUser user = WebUtils.getCurrentUser(session);
        String userKey = (user == null) ? "NONE" : ("USER" + user.getId());
        // ????????????session???????????????
        @SuppressWarnings("unchecked")
        Map<String, Boolean> popmap = (Map<String, Boolean>) session.getAttribute(USER_TARGETPOP);
        if (popmap == null || popmap.get(userKey) == null || !popmap.get(userKey)) {
            // ????????????session??????????????????????????????session??????????????????????????????????????????,?????????????????????
            isInitPopSesion = true;
        } else {
            isInitPopSesion = false;
        }
        if (popmap == null) {
            // ???session????????????????????????
            popmap = new HashMap<>();
            popmap.put(userKey, true);
            session.setAttribute(USER_TARGETPOP, popmap);
        }
        // ??????????????????????????????
        if (isInitPopSesion) {
            popmap.clear();
            popmap.put(userKey, true);
            if (user != null) {
                // -???????????????????????????
                List<String> targetIds = getUserTargetid(user.getId());
                for (String id : targetIds) {
                    // -??????session???
                    popmap.put(id, true);
                }
            }
        }
        // =----------------------------???????????????--------------------------------
        Boolean isHave = popmap.get(targetId);
        if (isHave == null) {
            // -???????????????????????????(?????????session?????????)
            Boolean isVisit = null;
            if (defaultAble) {
                isVisit = !isCtrl(targetId);
            } else {
                isVisit = false;
            }
            popmap.put(targetId, isVisit);
            return isVisit;
        } else {
            return isHave;
        }
    }

    /**
     * ??????????????????????????????????????????????????????????????????
     *
     * @param targetId
     * @return
     */
    private boolean isCtrl(String targetId) {
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<Pop> pops = popMapper.findBytargetId(targetId);
        if (pops.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public List<String> getUserTargetid(String userid) {
        // ?????????????????????????????????
        List<String> ids = new ArrayList<>();
        // ??????????????????
        Set<String> idset = new HashSet<String>();
        // ????????????????????????????????????????????????????????????????????????
        {
            Organization org = null;
            List<Post> posts = null;

            org = userServiceImpl.getUserOrganization(userid);
            posts = userServiceImpl.getPost(userid);
            String userSelect = null;
            String postSelect = null;
            String orgSelect = null;
            // ??????
            {
                userSelect = "SELECT TARGETID,ID FROM ALONE_AUTH_POP WHERE PSTATE='1' AND POPTYPE = '1'" + " AND OID ='"
                        + userid + "'";
            }
            // ????????????
            {
                if (org != null) {
                    orgSelect = "SELECT TARGETID,ID FROM ALONE_AUTH_POP WHERE PSTATE='1' AND POPTYPE = '2'" + " AND '"
                            + org.getTreecode() + "' LIKE CONCAT('%', OID, '%')";
                }
            }
            // ??????
            {
                if (posts != null) {
                    String postSql = "(";
                    for (Post post : posts) {
                        if (postSql.equals("(")) {
                            postSql = postSql + "'" + post.getId() + "'";
                        } else {
                            postSql = postSql + ",'" + post.getId() + "'";
                        }
                    }
                    postSql = postSql + ")";
                    if (posts.isEmpty()) {
                        postSql = "('NONE')";
                    }
                    postSelect = "SELECT TARGETID,ID FROM ALONE_AUTH_POP WHERE PSTATE='1' AND POPTYPE = '3'"
                            + " AND OID IN " + postSql + "";
                }
            }
            DataQuery query = DataQuery.getInstance("1", "TARGETID,ID",
                    "((" + userSelect + ") " + (postSelect == null ? "" : ("UNION(" + postSelect + ")"))
                            + (orgSelect == null ? "" : ("UNION(" + orgSelect + ")")) + " ) a");
            query.setPagesize(10000);
            query.setNoCount();
            query.setDistinct(true);
            try {
                for (Map<String, Object> node : query.search().getResultList()) {
                    idset.add((String) node.get("TARGETID"));
                }
            } catch (SQLException e) {
                log.error("search user pop by getUserTargetid", e);
                return new ArrayList<>();
            }
        }
        for (String typeid : idset) {
            ids.add(typeid);
        }
        return ids;
    }

}
