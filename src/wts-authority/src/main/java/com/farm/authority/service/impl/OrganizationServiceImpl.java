package com.farm.authority.service.impl;

import com.farm.authority.domain.*;
import com.farm.authority.mapper.*;
import com.farm.authority.service.OrganizationServiceInter;
import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBSort;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.sql.query.DataQuerys;
import com.farm.core.sql.result.DataResult;
import com.farm.core.time.TimeTool;
import com.farm.web.easyui.EasyUiTreeNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.*;

@Service
@Slf4j
public class OrganizationServiceImpl implements OrganizationServiceInter {

    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserpostMapper userpostMapper;
    @Autowired
    private PostactionMapper postactionMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserorgMapper userorgMapper;

    @Override
    @Transactional
    public Organization insertOrganizationEntity(Organization entity, LoginUser user) {
        entity.setCuser(user.getId());
        entity.setCtime(TimeTool.getTimeDate14());
        entity.setMuser(user.getId());
        entity.setUtime(TimeTool.getTimeDate14());
        entity.setState("1");
        if (StringUtils.isBlank(entity.getType())) {
            entity.setType("1");
        }
        if (entity.getParentid() == null) {
            entity.setParentid("NONE");
        }
        entity.setTreecode("NONE");
        organizationMapper.insertEntity(entity);
        initTreeCode(entity.getId());
        return entity;
    }

    @Override
    @Transactional
    public Organization editOrganizationEntity(Organization entity, LoginUser user) {
        Organization entity2 = organizationMapper.getEntity(entity.getId());
        entity2.setMuser(user.getId());
        entity2.setUtime(TimeTool.getTimeDate14());
        if (!StringUtils.isBlank(entity.getType())) {
            entity2.setType(entity.getType());
        }
        entity2.setSort(entity.getSort());
        entity2.setState(entity.getState());
        entity2.setComments(entity.getComments());
        entity2.setName(entity.getName());
        organizationMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteOrganizationEntity(String id, LoginUser user) {
        if (organizationMapper.selectEntitys(id).size() > 0) {
            throw new RuntimeException("????????????????????????????????????????????????");
        }
        // ????????????
        for (Post post : postMapper.findByOrganizationid(id)) {
            deletePostEntity(post.getId(), user);
        }
        // ??????????????????
        Organization org = organizationMapper.getEntity(id);
        organizationMapper.deleteEntity(org.getId());
    }

    @Override
    @Transactional
    public Organization getOrganizationEntity(String id) {
        if (id == null) {
            return null;
        }
        return organizationMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createOrganizationSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery.init(query, "ALONE_AUTH_ORGANIZATION",
                "ID,TYPE,SORT,PARENTID,MUSER,CUSER,STATE,UTIME,CTIME,COMMENTS,NAME,TREECODE,APPID");
        return dbQuery;
    }

    @Override
    @Transactional
    public void deletePostEntity(String id, LoginUser user) {
        String[] ids = id.split(",");
        for (String id1 : ids) {
            // ????????????????????????
            userpostMapper.deleteByPostid(id1);
            // ????????????????????????
            postactionMapper.deleteByPostid(id1);
            Post post = postMapper.getEntity(id1);
            postMapper.deleteEntity(post.getId());
        }
    }

    @Override
    @Transactional
    public Post getPostEntity(String id) {
        if (id == null) {
            return null;
        }
        return postMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createPostSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery.init(query,
                "ALONE_AUTH_POST as post left join alone_auth_organization as org on post.ORGANIZATIONID=org.ID",
                "post.ID AS ID, post.EXTENDIS AS EXTENDIS, post. NAME AS NAME, post.ORGANIZATIONID AS ORGANIZATIONID, post.PSTATE AS PSTATE, post.EUSER AS EUSER, post.EUSERNAME AS EUSERNAME, post.CUSER AS CUSER, post.CUSERNAME AS CUSERNAME, post.ETIME AS ETIME, post.CTIME AS CTIME, org. NAME AS ORGNAME");
        return dbQuery;
    }

    @Override
    @Transactional
    public void moveOrgTreeNode(String orgId, String targetOrgId, LoginUser currentUser) {
        String[] orgIds = orgId.split(",");
        for (int i = 0; i < orgIds.length; i++) {
            // ????????????
            Organization node = getOrganizationEntity(orgIds[i]);
            // if (node.getParentid().equals("NONE")) {
            // throw new RuntimeException("????????????????????????!");
            // }
            Organization target = getOrganizationEntity(targetOrgId);
            if (target.getTreecode().indexOf(node.getTreecode()) >= 0) {
                throw new RuntimeException("?????????????????????????????????!");
            }
            node.setParentid(targetOrgId);
            organizationMapper.editEntity(node);
            // ???????????????TREECODE
            List<Organization> list = organizationMapper.getAllSubNodes(orgIds[i] + "%");
            for (Organization org : list) {
                initTreeCode(org.getId());
            }
        }
    }

    private void initTreeCode(String treeNodeId) {
        Organization node = getOrganizationEntity(treeNodeId);
        if (node.getParentid().equals("NONE")) {
            node.setTreecode(node.getId());
        } else {
            node.setTreecode(organizationMapper.getEntity(node.getParentid()).getTreecode() + node.getId());
        }
        organizationMapper.editEntity(node);
    }

    @Override
    @Transactional
    public Post editPost(String postId, String postname, String extendis, LoginUser user) {
        // ????????????
        Post post = postMapper.getEntity(postId);
        post.setName(postname);
        post.setExtendis(extendis);
        post.setEtime(TimeTool.getTimeDate14());
        post.setEuser(user.getId());
        post.setEusername(user.getName());
        postMapper.editEntity(post);

        // ?????????????????????????????????????????????????????????????????????
        userpostMapper.deleteByPostid(post.getId());
        return post;
    }

    @Override
    @Transactional
    public Post insertPost(String orgId, String postname, String extendis, LoginUser user) {
        Post post = new Post();
        post.setName(postname);
        post.setExtendis(extendis);
        post.setCtime(TimeTool.getTimeDate14());
        post.setCuser(user.getId());
        post.setCusername(user.getName());
        post.setEtime(TimeTool.getTimeDate14());
        post.setEuser(user.getId());
        post.setEusername(user.getName());
        post.setOrganizationid(orgId);
        post.setPstate("1");
        postMapper.insertEntity(post);
        return post;
    }

    @Override
    @Transactional
    public List<EasyUiTreeNode> loadPostTree(String ids) {
        if (ids == null || ids.trim().length() <= 0) {
            ids = "NONE";
        }
        DataQuery query = DataQuery.getInstance("1", "NAME,PARENTID,ID,UTYPE",
                "(SELECT NAME,PARENTID,ID,'11' AS UTYPE,SORT FROM alone_auth_organization  UNION SELECT NAME,ORGANIZATIONID AS PARENTID,ID,TYPE AS UTYPE,1000 as SORT FROM alone_auth_post  ) a ");
        query.setPagesize(1000);
        query.addRule(new DBRule("PARENTID", ids, "="));
        query.setNoCount();
        query.addSort(new DBSort("UTYPE", "asc"));
        query.addSort(new DBSort("SORT", "asc"));
        DataQuery query2 = DataQuery.getInstance("1",
                "a.NAME as NAME,a.PARENTID as PARENTID,a.ID as ID,a.UTYPE as UTYPE",
                "alone_auth_organization b LEFT JOIN (SELECT NAME,PARENTID,ID,'0' AS UTYPE FROM alone_auth_organization  UNION SELECT NAME,ORGANIZATIONID AS PARENTID,ID,TYPE AS UTYPE FROM alone_auth_post  ) a ON a.PARENTID=b.ID");
        query2.setPagesize(1000);
        query2.addRule(new DBRule("b.PARENTID", ids, "="));
        query2.setNoCount();
        List<EasyUiTreeNode> list = null;
        try {
            List<Map<String, Object>> listOne = query.search().getResultList();
            for (Map<String, Object> node : listOne) {
                if (node.get("UTYPE").equals("1")) {
                    node.put("UTYPE", "icon-suppliers");
                }
                if (node.get("UTYPE").equals("2")) {
                    node.put("UTYPE", "icon-user_medical");
                }
            }
            list = EasyUiTreeNode.formatAsyncAjaxTree(listOne, query2.search().getResultList(), "PARENTID", "ID",
                    "NAME", "UTYPE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    @Transactional
    public void addUserPost(String userId, String postId, LoginUser currentUser) {
        User user = userMapper.getEntity(userId);
        if (user.getState().equals("2")) {
            throw new RuntimeException("?????????????????????");
        }
        Post post = postMapper.getEntity(postId);
        if (post == null) {
            throw new RuntimeException("????????????????????????");
        }
        for (Userpost userpost : userpostMapper.getTempUserPost(userId)) {
            if (userpost.getUserid().equals(userId) && userpost.getPostid().equals(postId)) {
                return;
            }
        }
        // ????????????
        Userpost userpost = new Userpost();
        userpost.setPostid(postId);
        userpost.setUserid(userId);
        userpostMapper.insertEntity(userpost);
    }

    @Override
    @Transactional
    public void removePostUsers(String postId, String userid, LoginUser currentUser) {
        String[] userIds = userid.split(",");
        for (String userId : userIds) {
            userpostMapper.deleteByUseridAndPostid(userId, postId);
        }
    }

    @Override
    @Transactional
    public void setPostActionTree(List<String> actionTreeIds, String postId) {
        if (postId == null || postId.trim().length() <= 0) {
            throw new IllegalArgumentException("?????????????????????");
        }
        // ?????????????????????
        postactionMapper.deleteByPostid(postId);
        for (String nodeId : actionTreeIds) {
            Postaction postaction = new Postaction();
            postaction.setMenuid(nodeId);
            postaction.setPostid(postId);
            postactionMapper.insertEntity(postaction);
        }
    }

    @Override
    @Transactional
    public List<Organization> getTree() {
        return organizationMapper.getList();
    }

    @Override
    public List<Map<String, Object>> getPostList(String orgId) {
        try {
            DataQuery query = DataQuery.getInstance(1,
                    "POST.ID AS POSTID, POST.NAME AS POSTNAME, ORG.ID AS ORGID, ORG.NAME AS ORGNAME ",
                    "ALONE_AUTH_POST POST " + "INNER JOIN ALONE_AUTH_ORGANIZATION ORG ON POST.ORGANIZATIONID = ORG.ID");
            query.setNoCount();
            query.addRule(new DBRule("POST.ORGANIZATIONID", orgId, "="));
            DataResult result = query.search();
            List<Map<String, Object>> list = result.getResultList();
            return list;

        } catch (SQLException e) {
            log.error("????????????", e);
        }
        return null;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getPostListWithPOrgPost(String orgId) {
        if (orgId == null || orgId.isEmpty()) {
            return new ArrayList<Map<String, Object>>();
        }
        DataQuerys.wipeVirus(orgId);
        Organization entity = organizationMapper.getEntity(orgId);
        String treecode = entity.getTreecode();
        String pOrgIds = "";
        for (int i = 1; i <= (treecode.length() - 32) / 32; i++) {
            pOrgIds += "'" + treecode.substring((i - 1) * 32, i * 32) + "'";
            if (i <= (treecode.length() - 64) / 32) {
                pOrgIds += ",";
            }
        }
        if (pOrgIds.isEmpty()) {
            pOrgIds = "''";
        }

        try {
            DataQuery query = DataQuery.getInstance(1,
                    "a.POSTID AS POSTID, a.POSTNAME AS POSTNAME, a.ORGID AS ORGID, a.ORGNAME AS ORGNAME ",
                    "(SELECT PPOST.ID AS POSTID, PPOST.NAME AS POSTNAME, PORG.ID AS ORGID, PORG.NAME AS ORGNAME "
                            + "FROM ALONE_AUTH_ORGANIZATION PORG "
                            + "LEFT JOIN ALONE_AUTH_POST PPOST ON PORG.ID = PPOST.ORGANIZATIONID "
                            + "WHERE PPOST.EXTENDIS = '1' AND PORG.ID IN (" + pOrgIds + ") /* ?????????????????????????????? */"
                            + "UNION ALL "
                            + "SELECT POST.ID AS POSTID, POST.NAME AS POSTNAME, ORG.ID AS ORGID, ORG.NAME AS ORGNAME "
                            + "FROM ALONE_AUTH_ORGANIZATION ORG "
                            + "LEFT JOIN ALONE_AUTH_POST POST ON ORG.ID = POST.ORGANIZATIONID " + "WHERE ORG.ID = '"
                            + orgId + "' /* ????????????????????????????????? */) a ");
            query.setNoCount();
            query.setPagesize(1000);
            query.addSqlRule("and  POSTID is not null");
            DataResult result = query.search();
            List<Map<String, Object>> list = result.getResultList();
            return list;
        } catch (SQLException e) {
            log.error("????????????", e);
        }
        return null;
    }

    @Override
    @Transactional
    public List<Organization> getParentOrgs(String orgid) {
        String id = orgid;
        List<Organization> orgs = new ArrayList<Organization>();
        while (id != null) {
            Organization centity = getOrganizationEntity(id);
            if (centity == null || centity.getParentid() == null || centity.getParentid().trim().length() <= 0) {
                id = null;
            } else {
                id = centity.getParentid();
            }
            if (centity != null) {
                orgs.add(centity);
            }
        }
        Collections.reverse(orgs);
        return orgs;
    }

    @Override
    @Transactional
    public List<String> getOrgUsers(String orgid) {
        DataQuerys.wipeVirus(orgid);
        List<String> list = new ArrayList<>();
        DataQuery query = DataQuery.getInstance(1, "a.id as id ,a.name as name",
                "ALONE_AUTH_USER a left join ALONE_AUTH_USERORG b on b.USERID=a.id left join ALONE_AUTH_ORGANIZATION c on c.ID=b.ORGANIZATIONID");
        query.setPagesize(1000);
        Organization org = getOrganizationEntity(orgid);
        if (org == null) {
            log.error("??????????????????id????????????????????????????????????????????????????????????????????????????????????????????????");
            return list;
        }
        query.addSqlRule(" and c.TREECODE like '" + org.getTreecode() + "%'");
        try {
            for (Map<String, Object> node : query.search().getResultList()) {
                list.add((String) node.get("ID"));
            }
        } catch (SQLException e) {
            log.error("?????????????????????????????????", e);
            return list;
        }
        return list;
    }

    @Override
    public List<String> getPostUser(String postid) {
        DataQuery query = DataQuery.getInstance(1, "POST.ID AS ID,USERPOST.USERID AS USERID",
                "ALONE_AUTH_POST AS POST LEFT JOIN ALONE_AUTH_USERPOST AS USERPOST ON POST.ID=USERPOST.POSTID");
        query.setPagesize(1000);
        query.addRule(new DBRule("post.ID", postid, "="));
        List<String> list = new ArrayList<>();
        try {
            for (Map<String, Object> node : query.search().getResultList()) {
                String userid = (String) node.get("USERID");
                if (userid != null) {
                    list.add((String) node.get("USERID"));
                }
            }
        } catch (SQLException e) {
            log.error("???????????????????????????", e);
            ;
            return list;
        }
        return list;
    }

    @Override
    @Transactional
    public List<Organization> getList() {
        return organizationMapper.getList();
    }

    @Override
    @Transactional
    public Organization getOrganizationByAppid(String appid) {
        List<Organization> orgs = organizationMapper.findByAppid(appid);
        if (orgs.size() > 0) {
            return orgs.get(0);
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void removeOrgUsers(String orgid, String userids, LoginUser currentUser) {
        String[] userIdArray = userids.split(",");
        for (String userId : userIdArray) {
            userorgMapper.deleteByUseridAndOrganizationid(userId, orgid);
            userpostMapper.deleteByUserid(userId);
        }
    }

    @Override
    @Transactional
    public void syncRemotOrgs(List<Organization> remoteOrgs) {
        Set<String> orgAppids = new HashSet<>();
        // ???????????????????????????????????????????????????????????????????????????
        Collections.sort(remoteOrgs, new Comparator<Organization>() {
            @Override
            public int compare(Organization o1, Organization o2) {
                return o2.getParentid().equals(o1.getId()) ? -1 : 1;
            }
        });
        for (Organization org : remoteOrgs) {
            orgAppids.add(org.getId());
            Organization localOrg = getOrganizationByAppid(org.getId());
            Organization parentOrg = getOrganizationByAppid(org.getParentid());
            if (localOrg != null) {
                // ??????
                localOrg.setName(org.getName());
                if (parentOrg == null) {
                    localOrg.setParentid("NONE");
                } else {
                    localOrg.setParentid(parentOrg.getId());
                }
                localOrg.setSort(org.getSort());
                localOrg.setState(org.getState());
                localOrg.setTreecode("NONE");
                // localOrg.setType(org.getType());
                organizationMapper.editEntity(localOrg);
            } else {
                // ??????
                if (org.getType() == null) {
                    org.setType("0");
                }
                org.setAppid(org.getId());
                if (parentOrg == null) {
                    org.setParentid("NONE");
                } else {
                    org.setParentid(parentOrg.getId());
                }
                organizationMapper.insertEntity(org);
            }
            // ?????????treecode
            initTreeCode(localOrg.getId());
        }
        for (Organization org : getList()) {
            if (org.getAppid() != null && !orgAppids.contains(org.getAppid())) {
                org.setState("0");
                organizationMapper.editEntity(org);
            }
        }
    }

    @Override
    @Transactional
    public List<String> getAllOrgComments() {
        List<String> list = organizationMapper.getAllOrgComments();
        return list;
    }

    @Override
    @Transactional
    public List<Organization> getOrganizationByComments(String orgComment) {
        List<Organization> orgs = organizationMapper.getOrganizationByComments(orgComment);
        return orgs;
    }

}