package com.farm.authority.service.impl;

import com.farm.authority.domain.Outuser;
import com.farm.authority.domain.User;
import com.farm.authority.mapper.OutuserMapper;
import com.farm.authority.mapper.UserMapper;
import com.farm.authority.service.OutuserServiceInter;
import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OutuserServiceImpl implements OutuserServiceInter {
    @Autowired
    private OutuserMapper outuserMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public Outuser insertOutuserEntity(Outuser entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        // entity.setCuser(user.getId());
        // entity.setCtime(TimeTool.getTimeDate14());
        // entity.setCusername(user.getName());
        // entity.setEuser(user.getId());
        // entity.setEusername(user.getName());
        // entity.setEtime(TimeTool.getTimeDate14());
        // entity.setPstate("1");
        outuserMapper.insertEntity(entity);
        return entity;
    }

    @Override
    @Transactional
    public Outuser editOutuserEntity(Outuser entity) {
        Outuser entity2 = outuserMapper.getEntity(entity.getId());
        entity2.setAccountname(entity.getAccountname());
        entity2.setAccountid(entity.getAccountid());
        entity2.setUserid(entity.getUserid());
        entity2.setPcontent(entity.getPcontent());
        entity2.setPstate(entity.getPstate());
        entity2.setCtime(entity.getCtime());
        entity2.setId(entity.getId());
        outuserMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteOutuserEntity(String id, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        outuserMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public Outuser getOutuserEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return outuserMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createOutuserSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery.init(query, "ALONE_AUTH_OUTUSER a left join ALONE_AUTH_USER b on a.userid=b.id",
                "a.ID as ID,ACCOUNTNAME,ACCOUNTID,USERID,a.PCONTENT as PCONTENT,a.PSTATE as PSTATE,a.CTIME as CTIME,NAME");
        return dbQuery;
    }

    @Override
    @Transactional
    public LoginUser getUserByAccountId(String outuserid, String name, String content) {
        // 通过外部用户id获得本地用户(如果没有则在关联表中创建对照关系，默认先不绑定用户)
        // 在表中找关联用户
        List<Outuser> outusers = outuserMapper.findByAccountid(outuserid);
        User user = null;
        if (outusers != null && outusers.size() > 0) {
            // 存在，查找用户id
            Outuser outuser = outusers.get(0);
            if (outuser.getUserid() != null && !outuser.getUserid().isEmpty()) {
                user = userMapper.getEntity(outuser.getUserid());
            }
        } else {
            // 创建关联关系实体，但是不绑定用户（用户后期绑定）
            Outuser outuser = new Outuser();
            outuser.setAccountid(outuserid);
            outuser.setAccountname(name);
            outuser.setCtime(TimeTool.getTimeDate14());
            outuser.setPcontent(content);
            outuser.setPstate("1");
            insertOutuserEntity(outuser, null);
        }
        if (user != null && user.getState().equals("2")) {
            user = null;
        }
        return user;
    }

    @Override
    @Transactional
    public Outuser getOutuserByAccountId(String outuserid) {
        // 通过外部用户id获得本地用户(如果没有则在关联表中创建对照关系，默认先不绑定用户)
        // 在表中找关联用户
        List<Outuser> outusers = outuserMapper.findByAccountid(outuserid);
        if (outusers.size() > 0) {
            return outusers.get(0);
        }
        return null;
    }

    @Override
    @Transactional
    public Outuser getOutuserByUserid(String readUserId, String contentLimitlike) {
        List<Outuser> outusers = outuserMapper.findOutuserByUserid(readUserId, contentLimitlike + "%");
        if (outusers.size() > 0) {
            return outusers.get(0);
        }
        return null;
    }

    @Override
    @Transactional
    public void bindUser(String outuserId, String userId) {
        Outuser user = outuserMapper.getEntity(outuserId);
        user.setUserid(userId);
        outuserMapper.editEntity(user);
    }

}
