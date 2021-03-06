package com.farm.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.FarmFileManagerInter.FILE_APPLICATION_TYPE;
import com.farm.exam.mapper.PaperChapterMapper;
import com.farm.exam.mapper.PaperSubjectMapper;
import com.farm.exam.mapper.SubjectMapper;
import com.farm.exam.domain.PaperChapter;
import com.farm.exam.domain.PaperSubject;
import com.farm.exam.domain.Subject;
import com.farm.exam.service.PaperChapterServiceInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class PaperChapterServiceImpl implements PaperChapterServiceInter {

    @Autowired
    private PaperChapterMapper paperChapterMapper;
    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private PaperSubjectMapper paperSubjectMapper;

    @Autowired
    private FarmFileManagerInter farmFileManagerImpl;

    @Override
    @Transactional
    public PaperChapter insertPaperchapterEntity(PaperChapter entity,
                                                 LoginUser user) {
        entity.setTreecode("NONE");
        paperChapterMapper.insertEntity(entity);
        initTreeCode(entity.getId());
        // 添加章节时限制不要超过3级别
        if (entity.getTreecode().length() > 96) {
            throw new RuntimeException("试卷章节不能超过3级！");
        }
        // --------------------------------------------------
        farmFileManagerImpl.submitFileByAppHtml(entity.getTextnote(),
                entity.getId(), FILE_APPLICATION_TYPE.PAPER_CHAPTERNOTE);
        return entity;
    }

    private void initTreeCode(String treeNodeId) {
        PaperChapter node = getPaperchapterEntity(treeNodeId);
        if (node.getParentid().equals("NONE")) {
            node.setTreecode(node.getId());
        } else {
            node.setTreecode(paperChapterMapper.getEntity(node.getParentid())
                    .getTreecode() + node.getId());
        }
        paperChapterMapper.editEntity(node);

    }

    @Override
    @Transactional
    public PaperChapter editPaperchapterEntity(PaperChapter entity,
                                               LoginUser user) {
        PaperChapter entity2 = paperChapterMapper.getEntity(entity.getId());
        String oldText = entity2.getTextnote();
        entity2.setTextnote(entity.getTextnote());
        entity2.setPaperid(entity.getPaperid());
        entity2.setParentid(entity.getParentid());
        entity2.setName(entity.getName());
        entity2.setSubjectpoint(entity.getSubjectpoint());
        entity2.setSubjectnum(entity.getSubjectnum());
        entity2.setSubjecttypeid(entity.getSubjecttypeid());
        entity2.setInitpoint(entity.getInitpoint());
        entity2.setPtype(entity.getPtype());
        entity2.setStype(entity.getStype());
        entity2.setId(entity.getId());
        entity2.setSort(entity.getSort());
        paperChapterMapper.editEntity(entity2);
        initTreeCode(entity2.getId());
        farmFileManagerImpl.updateFileByAppHtml(oldText, entity2.getTextnote(),
                entity2.getId(), FILE_APPLICATION_TYPE.PAPER_CHAPTERNOTE);
        return entity2;
    }

    @Override
    @Transactional
    public void deletePaperchapterEntity(String id, LoginUser user) {
        // 是否有子节点
        if (paperChapterMapper.findByParentId(id).size() > 0) {
            throw new RuntimeException("请先删除该章节得子节点!");
        }
        paperChapterMapper.deleteEntity(id);
        farmFileManagerImpl.cancelFilesByApp(id);
    }

    @Override
    @Transactional
    public PaperChapter getPaperchapterEntity(String id) {
        if (id == null) {
            return null;
        }
        return paperChapterMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createPaperchapterSimpleQuery(DataQuery query) {
        // TODO 自动生成代码,修改后请去除本注释
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_PAPER_CHAPTER",
                        "ID,TEXTNOTE,PAPERID,PARENTID,NAME,SUBJECTPOINT,SUBJECTNUM,SUBJECTTYPEID,INITPOINT,PTYPE,STYPE");
        return dbQuery;
    }

    @Override
    @Transactional
    public void addSubject(String subjectId, String chapterId,
                           LoginUser currentUser) {
        PaperChapter chapter = paperChapterMapper.getEntity(chapterId);
        Subject subject = subjectMapper.getEntity(subjectId);
        if (chapter == null) {
            throw new RuntimeException("该章节不存在！");
        }
        if (chapter.getPtype().equals("1")) {
            throw new RuntimeException("结构章节下不能放置试题！");
        }
        if (subject == null) {
            throw new RuntimeException("该试题不存在！");
        }
        if (isHasSubject(subjectId, chapter.getPaperid())) {
            throw new RuntimeException("该试题已经存在试卷中！");
        }
        int num = paperSubjectMapper.countByChapterid(chapterId);

        PaperSubject papersubject = new PaperSubject();
        papersubject.setChapterid(chapterId);
        papersubject.setPoint(0);
        papersubject.setSort(num + 1);
        papersubject.setPaperid(chapter.getPaperid());
        papersubject.setSubjectid(subject.getId());
        papersubject.setVersionid(subject.getVersionid());
        paperSubjectMapper.insertEntity(papersubject);
    }

    @Override
    @Transactional
    public boolean isHasSubject(String subjectId, String paperid) {
        List<PaperSubject> sameSubjects = paperSubjectMapper.findByPaperIdAndSubjectid(paperid, subjectId);

        if (sameSubjects.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional
    public void subjectSortUp(String id, LoginUser currentUser) {
        PaperSubject subrela = paperSubjectMapper.getEntity(id);
        // 查出章节下的所有题目
        List<PaperSubject> relas = paperSubjectMapper.findByChapterId(subrela.getChapterid());

        // 顺序排好// 和上一个交换位置
        int sit1 = 0;
        int sit2 = 0;
        Collections.sort(relas, new Comparator<PaperSubject>() {
            @Override
            public int compare(PaperSubject o1, PaperSubject o2) {
                return o1.getSort() - o2.getSort();
            }
        });
        for (int i = 0; i < relas.size(); i++) {
            if (relas.get(i).getId().equals(id)) {
                if (i == 0) {
                    return;
                }
                sit2 = i;
                sit1 = i - 1;
            }
            relas.get(i).setSort(i + 1);
        }
        for (int i = 0; i < relas.size(); i++) {
            if (i == sit1) {
                relas.get(i).setSort(i + 2);
            }
            if (i == sit2) {
                relas.get(i).setSort(i);
            }
            paperSubjectMapper.editEntity(relas.get(i));
        }
    }

    @Override
    @Transactional
    public PaperChapter insertPaperchapter(String paperid, String name,
                                           int sort, LoginUser user) {
        PaperChapter chapter = new PaperChapter();
        chapter.setName(name);
        chapter.setSort(sort);
        chapter.setPaperid(paperid);
        chapter.setStype("1");
        chapter.setParentid("NONE");
        chapter.setInitpoint(0);
        chapter.setPtype("2");
        return insertPaperchapterEntity(chapter, user);
    }

    @Override
    @Transactional
    public PaperChapter getEntity(String paperid, String name) {
        List<PaperChapter> list = paperChapterMapper.findByParentIdAndPtypeAndName(paperid, "2", name);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }
}
