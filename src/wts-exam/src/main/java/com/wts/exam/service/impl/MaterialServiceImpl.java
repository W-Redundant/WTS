package com.wts.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.FarmFileManagerInter.FILE_APPLICATION_TYPE;
import com.wts.exam.dao.MaterialDaoInter;
import com.wts.exam.dao.SubjectDaoInter;
import com.wts.exam.domain.Material;
import com.wts.exam.mapper.MaterialMapper;
import com.wts.exam.mapper.SubjectMapper;
import com.wts.exam.service.MaterialServiceInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/* *
 *功能：引用材料服务层实现类
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@Service
@Slf4j
public class MaterialServiceImpl implements MaterialServiceInter {
    @Resource
    private MaterialDaoInter materialDaoImpl;

    @Resource
    private SubjectDaoInter subjectDaoImpl;

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private SubjectMapper subjectMapper;

    @Autowired
    private FarmFileManagerInter farmFileManagerImpl;

    @Override
    @Transactional
    public Material insertMaterialEntity(Material entity, LoginUser user) {
        entity.setCuser(user.getId());
        entity.setCtime(TimeTool.getTimeDate14());
        entity.setEuser(user.getId());
        entity.setEtime(TimeTool.getTimeDate14());
        entity.setPstate("1");
        materialMapper.insertEntity(entity);
        farmFileManagerImpl.submitFileByAppHtml(entity.getText(),
                entity.getId(), FILE_APPLICATION_TYPE.SUBJECT_MATERIAL);
        return entity;
    }

    @Override
    @Transactional
    public Material editMaterialEntity(Material entity, LoginUser user) {
        Material entity2 = materialMapper.getEntity(entity.getId());
        String oldText = entity2.getText();
        entity2.setEuser(user.getId());
        entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setTitle(entity.getTitle());
        entity2.setText(entity.getText());
        entity2.setPcontent(entity.getPcontent());
        materialMapper.editEntity(entity2);
        farmFileManagerImpl.updateFileByAppHtml(oldText, entity2.getText(),
                entity2.getId(), FILE_APPLICATION_TYPE.SUBJECT_MATERIAL);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteMaterialEntity(String id, LoginUser user) {
        if (subjectMapper.countByMaterialid(id) > 0) {
            throw new RuntimeException("材料被引用无法删除!");
        }
        materialMapper.deleteEntity(id);
        farmFileManagerImpl.cancelFilesByApp(id);
    }

    @Override
    @Transactional
    public Material getMaterialEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return materialMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createMaterialSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "( SELECT A.ID AS ID, A.TITLE AS TITLE,A.UUID as UUID, COUNT(B.ID) AS RFNUM, A.CTIME AS CTIME, A.CUSER AS CUSER, C. NAME AS USERNAME FROM WTS_MATERIAL A LEFT JOIN WTS_SUBJECT B ON A.ID = B.MATERIALID LEFT JOIN ALONE_AUTH_USER C ON C.ID = A.CUSER GROUP BY A.ID ) T",
                        "ID,TITLE,RFNUM,CTIME,USERNAME,UUID");
        return dbQuery;
    }

}
