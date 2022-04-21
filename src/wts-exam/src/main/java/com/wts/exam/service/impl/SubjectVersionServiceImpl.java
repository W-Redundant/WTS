package com.wts.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.FarmFileManagerInter.FILE_APPLICATION_TYPE;
import com.wts.exam.dao.SubjectVersionDaoInter;
import com.wts.exam.domain.SubjectVersion;
import com.wts.exam.mapper.SubjectVersionMapper;
import com.wts.exam.service.SubjectVersionServiceInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SubjectVersionServiceImpl implements SubjectVersionServiceInter {
    @Resource
    private SubjectVersionDaoInter subjectversionDaoImpl;
    @Autowired
    private SubjectVersionMapper subjectVersionMapper;

    @Resource
    private FarmFileManagerInter farmFileManagerImpl;

    @Override
    @Transactional
    public SubjectVersion insertSubjectversionEntity(SubjectVersion entity,
                                                     LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        // entity.setCuser(user.getId());
        // entity.setCtime(TimeTool.getTimeDate14());
        // entity.setCusername(user.getName());
        // entity.setEuser(user.getId());
        // entity.setEusername(user.getName());
        // entity.setEtime(TimeTool.getTimeDate14());
        // entity.setPstate("1");
        subjectVersionMapper.insertEntity(entity);
        // --------------------------------------------------
        farmFileManagerImpl.submitFileByAppHtml(entity.getTipnote(),
                entity.getId(), FILE_APPLICATION_TYPE.SUBJECTNOTE);
        return entity;
    }

    @Override
    @Transactional
    public void deleteSubjectversionEntity(String id, LoginUser user) {
        subjectVersionMapper.deleteEntity(id);
        farmFileManagerImpl.cancelFilesByApp(id);
    }

    @Override
    @Transactional
    public SubjectVersion getSubjectversionEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return subjectVersionMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createSubjectversionSimpleQuery(DataQuery query) {
        // TODO 自动生成代码,修改后请去除本注释
        DataQuery dbQuery = DataQuery
                .init(query, "WTS_SUBJECT_VERSION",
                        "ID,TIPTYPE,SUBJECTID,TIPNOTE,TIPSTR,PCONTENT,PSTATE,CUSER,CUSERNAME,CTIME");
        return dbQuery;
    }

}
