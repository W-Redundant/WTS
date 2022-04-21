package com.farm.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.FarmFileManagerInter.FILE_APPLICATION_TYPE;
import com.farm.exam.mapper.SubjectAnalysisMapper;
import com.farm.exam.domain.SubjectAnalysis;
import com.farm.exam.service.SubjectAnalysisServiceInter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubjectAnalysisServiceImpl implements SubjectAnalysisServiceInter {

    @Autowired
    private SubjectAnalysisMapper subjectAnalysisMapper;

    @Autowired
    private FarmFileManagerInter farmFileManagerImpl;


    @Override
    @Transactional
    public SubjectAnalysis insertSubjectAnalysisEntity(SubjectAnalysis entity,
                                                       LoginUser user) {
        entity.setCuser(user.getId());
        entity.setCtime(TimeTool.getTimeDate14());
        entity.setCusername(user.getName());
        subjectAnalysisMapper.insertEntity(entity);
        farmFileManagerImpl.submitFileByAppHtml(entity.getText(),
                entity.getId(), FILE_APPLICATION_TYPE.SUBJECT_ANALYSIS);
        return entity;
    }

    @Override
    @Transactional
    public SubjectAnalysis editSubjectAnalysisEntity(SubjectAnalysis entity,
                                                     LoginUser user) {
        SubjectAnalysis entity2 = subjectAnalysisMapper.getEntity(entity
                .getId());
        String oldText = entity2.getText();
        entity2.setText(entity.getText());
        entity2.setPcontent(entity.getPcontent());
        entity2.setPstate(entity.getPstate());
        subjectAnalysisMapper.editEntity(entity2);
        farmFileManagerImpl.updateFileByAppHtml(oldText, entity2.getText(),
                entity2.getId(), FILE_APPLICATION_TYPE.SUBJECT_ANALYSIS);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteSubjectAnalysisEntity(String id, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        subjectAnalysisMapper.deleteEntity(id);
        farmFileManagerImpl.cancelFilesByApp(id);
    }

    @Override
    @Transactional
    public SubjectAnalysis getSubjectAnalysisEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return subjectAnalysisMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createSubjectAnalysisSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery.init(query, "WTS_SUBJECT_ANALYSIS",
                "ID,SUBJECTID,TEXT,PCONTENT,PSTATE,CUSERNAME,CTIME");
        return dbQuery;
    }

    @Override
    @Transactional
    public List<SubjectAnalysis> getSubjectAnalysies(String subjectid) {
        return subjectAnalysisMapper.findBySubjectId(subjectid);
    }
}
