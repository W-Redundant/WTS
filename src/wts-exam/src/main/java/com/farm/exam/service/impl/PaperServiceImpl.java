package com.farm.exam.service.impl;

import com.farm.exam.domain.Material;
import com.farm.exam.domain.Paper;
import com.farm.exam.domain.PaperChapter;
import com.farm.exam.domain.PaperSubject;
import com.farm.exam.domain.Subject;
import com.farm.exam.domain.SubjectAnalysis;
import com.farm.exam.domain.SubjectAnswer;
import com.farm.exam.domain.SubjectType;
import com.farm.exam.domain.SubjectVersion;
import com.farm.exam.domain.ex.ChapterUnit;
import com.farm.exam.domain.ex.FileJsonBean;
import com.farm.exam.domain.ex.WtsPaperBean;
import com.farm.exam.domain.ex.PaperUnit;
import com.farm.exam.domain.ex.SubjectUnit;
import com.farm.exam.domain.ex.TipType;
import com.farm.core.time.TimeTool;
//import com.farm.doc.dao.FarmDocfileDaoInter;
import com.farm.doc.domain.Docfile;
import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.FarmFileManagerInter.FILE_APPLICATION_TYPE;
import com.farm.doc.server.FarmFileManagerInter.FILE_TYPE;
import com.farm.doc.server.commons.FarmDocFiles;
import com.farm.parameter.FarmParameterService;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.farm.exam.dao.PaperDaoInter;
import com.farm.exam.dao.MaterialDaoInter;
import com.farm.exam.dao.PaperChapterDaoInter;
import com.farm.exam.dao.PaperSubjectDaoInter;
import com.farm.exam.dao.PaperUserOwnDaoInter;
import com.farm.exam.dao.RoomPaperDaoInter;
import com.farm.exam.dao.SubjectAnalysisDaoInter;
import com.farm.exam.dao.SubjectAnswerDaoInter;
import com.farm.exam.dao.SubjectDaoInter;
import com.farm.exam.dao.SubjectTypeDaoInter;
import com.farm.exam.dao.SubjectVersionDaoInter;
import com.farm.exam.service.ExamTypeServiceInter;
import com.farm.exam.service.MaterialServiceInter;
import com.farm.exam.service.PaperChapterServiceInter;
import com.farm.exam.service.PaperServiceInter;
import com.farm.exam.service.PaperSubjectServiceInter;
import com.farm.exam.service.SubjectAnswerServiceInter;
import com.farm.exam.service.SubjectServiceInter;
import com.farm.exam.service.SubjectTypeServiceInter;
import com.farm.exam.service.SubjectVersionServiceInter;
import com.farm.exam.utils.WtsPaperBeanUtils;
import com.farm.exam.utils.WordPaperCreator;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DBSort;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.sql.query.DataQuerys;
import com.farm.core.sql.result.DataResult;
import com.farm.core.sql.result.ResultsHandle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import com.farm.core.auth.domain.LoginUser;

/* *
 *?????????????????????????????????
 *?????????
 *
 *?????????v0.1
 *?????????FarmCode????????????
 *?????????20150707114057
 *?????????
 */
@Service
public class PaperServiceImpl implements PaperServiceInter {
    @Autowired
    private PaperDaoInter paperDaoImpl;
    @Autowired
    private PaperSubjectDaoInter papersubjectDaoImpl;
    @Autowired
    private PaperChapterDaoInter paperchapterDaoImpl;
    @Autowired
    private RoomPaperDaoInter roompaperDaoImpl;
    @Autowired
    private SubjectServiceInter subjectServiceImpl;
    @Autowired
    private FarmFileManagerInter farmFileManagerImpl;
    @Autowired
    private SubjectVersionDaoInter subjectversionDaoImpl;
    @Autowired
    private SubjectAnswerServiceInter subjectAnswerServiceImpl;
    @Autowired
    private SubjectAnswerDaoInter subjectanswerDaoImpl;
    @Autowired
    private SubjectVersionServiceInter subjectVersionServiceImpl;
    @Autowired
    private MaterialServiceInter materialServiceImpl;
    @Autowired
    private SubjectTypeDaoInter subjecttypeDaoImpl;
    @Autowired
    private PaperChapterServiceInter paperChapterServiceImpl;
    @Autowired
    private PaperSubjectServiceInter paperSubjectServiceImpl;
    @Autowired
    private ExamTypeServiceInter examTypeServiceImpl;
    @Autowired
    private SubjectTypeServiceInter subjectTypeServiceImpl;
    @Autowired
    private PaperUserOwnDaoInter paperuserownDaoImpl;
    @Autowired
    private SubjectDaoInter subjectDaoImpl;
    @Autowired
    private SubjectAnalysisDaoInter SubjectAnalysisDaoImpl;
    @Autowired
    private MaterialDaoInter materialDaoImpl;
//    @Autowired
//    private FarmDocfileDaoInter farmDocfileDao;
    private static final Logger log = Logger.getLogger(PaperServiceImpl.class);

    @Override
    @Transactional
    public Paper insertPaperEntity(Paper entity, LoginUser user) {
        entity.setCuser(user.getId());
        entity.setCtime(TimeTool.getTimeDate14());
        entity.setCusername(user.getName());
        entity.setEuser(user.getId());
        entity.setEusername(user.getName());
        entity.setEtime(TimeTool.getTimeDate14());
        entity.setAvgpoint(0);
        entity.setCompletetnum(0);
        entity.setBooknum(0);
        entity.setLowpoint(0);
        entity.setToppoint(0);
        entity.setSubjectnum(0);
        entity.setPointnum(0);
        if (StringUtils.isBlank(entity.getExamtypeid())) {
            entity.setExamtypeid(null);
        }
        entity = paperDaoImpl.insertEntity(entity);
        // --------------------------------------------------
        farmFileManagerImpl.submitFileByAppHtml(entity.getPapernote(),
                entity.getId(), FILE_APPLICATION_TYPE.PAPERNOTE);
        return entity;
    }

    @Override
    @Transactional
    public Paper editPaperEntity(Paper entity, LoginUser user) {
        Paper entity2 = paperDaoImpl.getEntity(entity.getId());
        String oldText = entity2.getPapernote();
        entity2.setEuser(user.getId());
        entity2.setEusername(user.getName());
        entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setPapernote(entity.getPapernote());
        entity2.setAdvicetime(entity.getAdvicetime());
        entity2.setPstate(entity.getPstate());
        entity2.setPcontent(entity.getPcontent());
        entity2.setName(entity.getName());
        entity2.setExamtypeid(entity.getExamtypeid());
        if (StringUtils.isBlank(entity2.getExamtypeid())) {
            entity2.setExamtypeid(null);
        }
        paperDaoImpl.editEntity(entity2);
        farmFileManagerImpl.updateFileByAppHtml(oldText, entity.getPapernote(),
                entity2.getId(), FILE_APPLICATION_TYPE.PAPERNOTE);
        return entity2;
    }

    @Override
    @Transactional
    public void deletePaperEntity(String id, LoginUser user) {
        // ?????? ???/??????/???????????????
        papersubjectDaoImpl.deleteEntitys(DBRuleList.getInstance()
                .add(new DBRule("PAPERID", id, "=")).toList());
        paperchapterDaoImpl.deleteEntitys(DBRuleList.getInstance()
                .add(new DBRule("PAPERID", id, "=")).toList());
        roompaperDaoImpl.deleteEntitys(DBRuleList.getInstance()
                .add(new DBRule("PAPERID", id, "=")).toList());
        paperDaoImpl.deleteEntity(paperDaoImpl.getEntity(id));
        paperuserownDaoImpl.deleteEntitys(DBRuleList.getInstance()
                .add(new DBRule("PAPERID", id, "=")).toList());
        farmFileManagerImpl.cancelFilesByApp(id);
    }

    @Override
    @Transactional
    public Paper getPaperEntity(String id) {
        // TODO ??????????????????,???????????????????????????
        if (id == null) {
            return null;
        }
        return paperDaoImpl.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createPaperSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_PAPER a LEFT JOIN WTS_EXAM_TYPE b ON a.EXAMTYPEID = b.id left join alone_auth_user c on a.CUSER=c.ID",
                        "a.ID as ID,a.UUID as UUID,c.name as USERNAME,a.PAPERNOTE as PAPERNOTE,a.ADVICETIME as ADVICETIME,a.LOWPOINT as LOWPOINT,a.TOPPOINT as TOPPOINT,a.AVGPOINT as AVGPOINT,a.COMPLETETNUM as COMPLETETNUM,a.POINTNUM as POINTNUM,a.SUBJECTNUM as SUBJECTNUM,a.PSTATE as PSTATE,a.PCONTENT as PCONTENT,a.NAME as NAME,a.EUSER as EUSER,a.EUSERNAME as EUSERNAME,a.CUSER as CUSER,a.CUSERNAME as CUSERNAME,a.ETIME as ETIME,a.CTIME as CTIME,a.EXAMTYPEID as EXAMTYPEID,b.name as TYPENAME");
        return dbQuery;
    }

    @Override
    @Transactional
    public void examTypeSetting(String roomid, String examtypeId,
                                LoginUser currentUser) {
        if (StringUtils.isNotBlank(examtypeId)) {
            Paper entity2 = paperDaoImpl.getEntity(roomid);
            entity2.setExamtypeid(examtypeId);
            entity2.setEtime(TimeTool.getTimeDate14());
            entity2.setEuser(currentUser.getId());
            entity2.setEusername(currentUser.getName());
            paperDaoImpl.editEntity(entity2);
        }
    }

    @Override
    @Transactional
    public void editState(String id, String state, LoginUser currentUser) {
        Paper entity2 = paperDaoImpl.getEntity(id);
        entity2.setPstate(state);
        entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setEuser(currentUser.getId());
        entity2.setEusername(currentUser.getName());
        paperDaoImpl.editEntity(entity2);
    }

    @Override
    @Transactional
    public PaperUnit getPaperUnit(String paperId) {
        PaperUnit paper = new PaperUnit();
        paper.setInfo(getPaperEntity(paperId));
        // ???????????????????????????
        List<PaperChapter> chapters = paperchapterDaoImpl
                .selectEntitys(DBRuleList.getInstance()
                        .add(new DBRule("PAPERID", paperId, "=")).toList());

        // ??????????????????????????????
        List<PaperSubject> subjectRelations = papersubjectDaoImpl
                .selectEntitys(DBRuleList.getInstance()
                        .add(new DBRule("PAPERID", paperId, "=")).toList());

        // ????????????????????????????????????
        Collections.sort(subjectRelations, new Comparator<PaperSubject>() {
            @Override
            public int compare(PaperSubject o1, PaperSubject o2) {
                return o1.getSort() - o2.getSort();
            }
        });

        // ???
        Map<String, SubjectUnit> subjects = new HashMap<>();
        for (PaperSubject sub : subjectRelations) {
            SubjectUnit subjectUnit = subjectServiceImpl.getSubjectUnit(sub
                    .getVersionid());
            subjects.put(sub.getVersionid(), subjectUnit);
        }
        // ????????????
        List<ChapterUnit> rootChapteUnits = new ArrayList<>();
        int allPoint = 0;
        for (PaperChapter chapter : chapters) {
            if (chapter.getParentid().equals("NONE")) {
                // ?????????????????????????????????
                ChapterUnit chapterUnit = getChapterUnit(subjectRelations,
                        subjects, chapter, chapters);
                rootChapteUnits.add(chapterUnit);
                allPoint = allPoint + chapterUnit.getAllpoint();
            }
        }
        // ????????????
        Collections.sort(rootChapteUnits, new Comparator<ChapterUnit>() {
            @Override
            public int compare(ChapterUnit o1, ChapterUnit o2) {
                return o1.getChapter().getSort() - o2.getChapter().getSort();
            }
        });
        paper.setChapters(rootChapteUnits);
        paper.setAllPoint(allPoint);
        paper.setSubjectNum(subjectRelations.size());
        paper.setRootChapterNum(rootChapteUnits.size());
        return paper;
    }

    /**
     * ??????????????????????????????
     *
     * @param subjects ??????????????????
     * @param chapter  ??????????????????
     * @param chapters ??????????????????
     * @return
     */
    private ChapterUnit getChapterUnit(List<PaperSubject> subjectRelations,
                                       Map<String, SubjectUnit> subjects, PaperChapter chapter,
                                       List<PaperChapter> chapters) {
        ChapterUnit unit = new ChapterUnit();
        int allpoint = 0;
        int subjectNum = 0;
        unit.setChapter(chapter);
        // ????????????????????????
        List<ChapterUnit> chapterUnits = new ArrayList<>();
        {// ?????????????????????unit??????
            for (PaperChapter chapternode : chapters) {
                if (chapternode.getParentid().equals(chapter.getId())) {
                    ChapterUnit chapterunit = getChapterUnit(subjectRelations,
                            subjects, chapternode, chapters);
                    chapterUnits.add(chapterunit);
                    subjectNum = subjectNum + chapterunit.getSubjectNum();
                    allpoint = allpoint + chapterunit.getAllpoint();
                }
            }
        }
        Collections.sort(chapterUnits, new Comparator<ChapterUnit>() {
            @Override
            public int compare(ChapterUnit o1, ChapterUnit o2) {
                return o1.getChapter().getSort() - o2.getChapter().getSort();
            }
        });
        unit.setChapters(chapterUnits);
        // ??????????????????
        List<SubjectUnit> subjectunits = new ArrayList<>();
        {
            for (PaperSubject relation : subjectRelations) {
                if (relation.getChapterid().equals(chapter.getId())) {
                    SubjectUnit subjectunit = subjects.get(relation
                            .getVersionid());
                    subjectunit.setPoint(relation.getPoint());
                    subjectunits.add(subjectunit);
                    allpoint = allpoint + relation.getPoint();
                }
            }
        }
        // ?????????????????????
        List<Material> materials = new ArrayList<>();
        Set<String> materialids = new HashSet<>();
        for (SubjectUnit subunit : subjectunits) {
            if (StringUtils.isNotBlank(subunit.getSubject().getMaterialid())) {
                Material material = materialServiceImpl
                        .getMaterialEntity(subunit.getSubject().getMaterialid());
                if (!materialids.contains(material.getId())) {
                    materials.add(material);
                    materialids.add(material.getId());
                }
            }
        }
        unit.setMaterials(materials);
        unit.setSubjects(subjectunits);
        unit.setAllpoint(allpoint);
        unit.setSubjectNum(subjectNum + subjectunits.size());
        return unit;
    }

    @Override
    @Transactional
    public void refreshSubjectNum(String paperid) {
        int num = papersubjectDaoImpl.countEntitys(DBRuleList.getInstance()
                .add(new DBRule("PAPERID", paperid, "=")).toList());
        Paper paper = paperDaoImpl.getEntity(paperid);
        paper.setSubjectnum(num);
        paperDaoImpl.editEntity(paper);
    }

    @Override
    @Transactional
    public boolean isAllSubjectHavePoint(String paperid) {
        boolean isAllSubjectHavePoint = true;
        // ???????????????????????????????????????????????????
        List<PaperSubject> subjects = papersubjectDaoImpl
                .selectEntitys(DBRuleList.getInstance()
                        .add(new DBRule("PAPERID", paperid, "=")).toList());
        for (PaperSubject subject : subjects) {
            if (subject.getPoint() == null || subject.getPoint() == 0) {
                // ??????????????????
                isAllSubjectHavePoint = false;
            }
        }
        return isAllSubjectHavePoint;
    }

    @Override
    @Transactional
    public boolean isAllHaveObjectiveSubjectAnswer(String paperid) {
        boolean isAllHaveObjectiveSubjectAnswer = true;
        List<PaperSubject> subjects = papersubjectDaoImpl
                .selectEntitys(DBRuleList.getInstance()
                        .add(new DBRule("PAPERID", paperid, "=")).toList());
        for (PaperSubject subject : subjects) {
            // ??????????????????
            SubjectVersion version = subjectVersionServiceImpl
                    .getSubjectversionEntity(subject.getVersionid());
            // TIPTYPE:1.?????????2.?????????3.?????????4?????????5??????
            if (version.getTiptype().equals("2")
                    || version.getTiptype().equals("3")
                    || version.getTiptype().equals("4")) {
                // ???????????????????????????
                int answerNum = subjectanswerDaoImpl.countEntitys(DBRuleList
                        .getInstance()
                        .add(new DBRule("VERSIONID", version.getId(), "="))
                        .toList());
                if (answerNum <= 0) {
                    isAllHaveObjectiveSubjectAnswer = false;
                }
            }
        }
        return isAllHaveObjectiveSubjectAnswer;
    }

    @Override
    @Transactional
    public boolean isHaveSubjects(String paperid) {
        List<PaperSubject> subjects = papersubjectDaoImpl
                .selectEntitys(DBRuleList.getInstance()
                        .add(new DBRule("PAPERID", paperid, "=")).toList());
        return subjects.size() > 0;
    }

    @Override
    public List<SubjectUnit> getPaperSubjects(List<ChapterUnit> chapterUnits) {
        List<SubjectUnit> list = new ArrayList<>();
        for (ChapterUnit unit : chapterUnits) {
            List<SubjectUnit> chapterSubs = getPaperSubjects(unit.getChapters());
            if (chapterSubs != null && chapterSubs.size() > 0) {
                list.addAll(chapterSubs);
            }
            if (unit.getSubjects() != null && unit.getSubjects().size() > 0) {
                list.addAll(unit.getSubjects());
            }
        }
        return list;
    }

    @Override
    @Transactional
    public int getBookNum(String paperid) {
        Paper paper = paperDaoImpl.getEntity(paperid);
        return paper.getBooknum();
    }

    @Override
    @Transactional
    public void updateBooknum(String paperid, int countEntitys) {
        Paper paper = paperDaoImpl.getEntity(paperid);
        paper.setBooknum(countEntitys);
        paperDaoImpl.editEntity(paper);
    }

    @Override
    @Transactional
    public String addRandomSubjects(String paperid, String typeid,
                                    String tiptype, Integer subnum, Integer point, LoginUser currentUser) {
        String warnTip = "";
        // ??????(typeid/tiptype??????1000?????????ID)
        List<String> allsubs = get1000Subject(typeid, tiptype,
                currentUser.getId());
        List<String> catchSubs = new ArrayList<>();
        // ????????????
        Collections.shuffle(allsubs);
        int catchNum = subnum;
        if (allsubs.size() < catchNum) {
            // ????????????
            catchNum = allsubs.size();
            warnTip = warnTip + "???????????????" + typeid + "??????????????????(" + catchNum + ")";
        }
        // ???????????????
        for (; catchNum > 0; catchNum--) {
            catchSubs.add(allsubs.get(catchNum - 1));
        }
        // ????????????
        {
            // -----?????????????????????
            TipType type = TipType.getTipType(tiptype);
            PaperChapter chapter = paperChapterServiceImpl.getEntity(paperid,
                    type.getTitle());
            if (chapter == null) {
                chapter = paperChapterServiceImpl.insertPaperchapter(paperid,
                        type.getTitle(), Integer.valueOf(type.getType()),
                        currentUser);
            }
            for (String subjectId : catchSubs) {
                if (!paperChapterServiceImpl.isHasSubject(subjectId, paperid)) {
                    // -----????????????
                    paperChapterServiceImpl.addSubject(subjectId,
                            chapter.getId(), currentUser);
                    // -----????????????
                    editPoint(paperid, chapter.getId(), subjectId, point,
                            currentUser);
                } else {
                    warnTip = warnTip + "/??????(" + paperid + ")?????????????????????("
                            + subjectId + ")";
                }
            }
        }
        return warnTip;
    }

    private List<String> get1000Subject(String typeid, String tiptype,
                                        String userid) {
        final List<String> resultList = new ArrayList<>();
        DataQuery query = DataQuery
                .getInstance(
                        1,
                        "b.ID as SUBJECTID,a.NAME as TYPENAME",
                        "WTS_SUBJECT_TYPE a left join WTS_SUBJECT b on b.TYPEID=a.ID left join WTS_SUBJECT_VERSION c on b.VERSIONID=c.id");
        query.setPagesize(1000);
        {
            // ??????????????????
            if (typeid != null && !typeid.equals("NONE")) {
                SubjectType type = subjecttypeDaoImpl.getEntity(typeid);
                query.addSqlRule("and a.TREECODE like '" + type.getTreecode()
                        + "%' ");
            }
        }
        {
            // ????????????
            query.addSqlRule("and b.id is not null and b.PSTATE='1' and c.TIPTYPE='"
                    + tiptype + "'");
        }
        {
            // ????????????
            String typeids_Rule = DataQuerys
                    .parseSqlValues(subjectTypeServiceImpl.getUserPopTypeids(
                            userid, "1"));
            if (typeids_Rule != null) {
                query.addSqlRule(" and a.id in (" + typeids_Rule + ")");
            }
        }
        query.addDefaultSort(new DBSort("C.CTIME", "desc"));
        try {
            DataResult result = query.search();
            result.runHandle(new ResultsHandle() {
                @Override
                public void handle(Map<String, Object> row) {
                    resultList.add((String) row.get("SUBJECTID"));
                }
            });
            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
            return resultList;
        }
    }

    @Override
    @Transactional
    public void editPoint(String paperid, String chapterid, String subjectid,
                          int point, LoginUser user) {
        PaperSubject psub = paperSubjectServiceImpl.getPapersubjectEntity(
                paperid, chapterid, subjectid);
        psub.setPoint(point);
        psub = paperSubjectServiceImpl.editPapersubjectEntity(psub, user);
    }

    @Override
    @Transactional
    public void clearPaper(String paperid) {
        List<DBRule> rules = DBRuleList.getInstance()
                .add(new DBRule("PAPERID", paperid, "=")).toList();
        papersubjectDaoImpl.deleteEntitys(rules);
        paperchapterDaoImpl.deleteEntitys(rules);
    }

    /**
     * ???????????????????????????????????????
     *
     * @param user
     * @return
     */
    private File getUserPaperFile(LoginUser user, String exname) {
        // ???????????????
        String fileDirPath = FarmParameterService.getInstance().getParameter(
                "config.doc.dir");
        // ????????????????????????
        String uesrDirPath = fileDirPath + File.separator + "user"
                + user.getId();
        new File(uesrDirPath).mkdirs();
        // ??????????????????
        String papaerFilePath = uesrDirPath + File.separator + "paper."
                + exname;
        File papaerFile = new File(papaerFilePath);
        if (papaerFile.exists()) {
            papaerFile.delete();
        }
        return papaerFile;
    }

    @Override
    @Transactional
    public File exprotWord(PaperUnit paper, LoginUser user) {
        // ??????????????????docx??????
        File papaerFile = getUserPaperFile(user, "docx");
        FileOutputStream out = null;
        XWPFDocument document = null;
        try {
            document = new XWPFDocument();
            out = new FileOutputStream(papaerFile);
            WordPaperCreator.initWordPaper(document, paper);
            document.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return papaerFile;
    }

    @Override
    @Transactional
    public File exprotWtsp(String paperId, LoginUser user) throws IOException {
        if (StringUtils.isBlank(paperId)) {
            throw new RuntimeException("the papaerId is not exist:" + paperId);
        }
        // ??????????????????docx??????
        File papaerFile = getUserPaperFile(user, "wtsp");
        WtsPaperBean paperj = new WtsPaperBean();
        paperj.setPaper(paperDaoImpl.getEntity(paperId));
        paperj.setChapters(paperchapterDaoImpl.selectEntitys(DBRuleList
                .getInstance().add(new DBRule("PAPERID", paperId, "="))
                .toList()));
        paperj.setPaperSubjects(papersubjectDaoImpl.selectEntitys(DBRuleList
                .getInstance().add(new DBRule("PAPERID", paperId, "="))
                .toList()));
        paperj.setSubjects(subjectDaoImpl.getSubjectsByPaperId(paperId));
        paperj.setSubjectVersions(subjectversionDaoImpl
                .getVersionsByPaperId(paperId));
        paperj.setSubjectAnalysies(SubjectAnalysisDaoImpl
                .getAnalysisByPaperId(paperId));
        paperj.setSubjectAnswers(subjectanswerDaoImpl
                .getAnswersByPaperId(paperId));
        paperj.setMaterials(materialDaoImpl.getMaterialsByPaperId(paperId));
//        List<Docfile> files = farmDocfileDao.getfilesByAppids(paperj.getAllAppid());
        List<Docfile> files = new ArrayList<>();
        List<FileJsonBean> jsonfiles = new ArrayList<>();
        for (Docfile file : files) {
            FileJsonBean jfile = new FileJsonBean();
            File drivefile = farmFileManagerImpl.getFile(file);
            if (drivefile.exists()) {
                try {
                    String base64File = FarmDocFiles
                            .encodeBase64File(drivefile);
                    if (file.getLen() < (10 * 1024 * 1024)) {
                        jfile.setBase64(base64File);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            jfile.setInfo(file);
            jsonfiles.add(jfile);
        }
        paperj.setFiles(jsonfiles);
        WtsPaperBeanUtils.writeToFile(papaerFile, paperj);
        return papaerFile;
    }

    @Override
    @Transactional
    public String importByWtsPaperBean(WtsPaperBean bean, String examTypeId,
                                       String subjectTypeId, LoginUser user) {
        // ????????????????????????
        String paperUuid = bean.getPaper().getUuid();
        Paper existPaper = paperDaoImpl.getEntityByUuid(paperUuid);
        if (existPaper != null) {
            // ?????????????????????
            throw new RuntimeException("????????????????????????" + existPaper.getName() + ":"
                    + existPaper.getId());
        }
        // ?????????ID??????ID?????????
        Map<String, String> idDic = new HashMap<>();
        // ?????????id??????id?????????
        Map<String, String> fileIdDic = new HashMap<>();
        // ?????????id??????id?????????
        Map<String, String> chapterIdDic = new HashMap<>();
        // // --?????????(????????????base64??????)
        // ??????
        for (FileJsonBean file : bean.getFiles()) {
            byte[] fileByte = Base64.decodeBase64(file.getBase64());
            String newid = farmFileManagerImpl.saveFile(fileByte, FILE_TYPE
                    .parseType(file.getInfo().getType()), file.getInfo()
                    .getName(), user);
            idDic.put(file.getInfo().getId(), newid);
            fileIdDic.put(file.getInfo().getId(), newid);
        }
        // // --??????5??????
        // ??????
        for (Material material : bean.getMaterials()) {
            String oid = material.getId();
            String existId = materialDaoImpl.getIdByUuid(material.getUuid());
            if (existId != null) {
                // ????????????????????????
                idDic.put(oid, existId);
            } else {
                material.setId(null);
                material.setCuser(user.getId());
                material.setEuser(user.getId());
                material.setText(raplaceWtspFileId(material.getText(),
                        fileIdDic, "?????????"));
                material = materialDaoImpl.insertEntity(material);
                idDic.put(oid, material.getId());
            }
        }
        // ???
        for (Subject subject : bean.getSubjects()) {
            // 1.???
            String subjectoid = subject.getId();
            Subject existSubject = subjectDaoImpl.getEntityByUuid(subject
                    .getUuid());
            if (existSubject != null) {
                // ??????????????????????????????
                idDic.put(subjectoid, existSubject.getId());
                idDic.put(subject.getVersionid(), existSubject.getVersionid());
            } else {
                subject.setId(null);
                subject.setTypeid(subjectTypeId);
                subject.setMaterialid(idDic.get(subject.getMaterialid()));
                subject.setPraisenum(0);
                subject.setCommentnum(0);
                subject.setDonum(0);
                subject.setRightnum(0);
                subject = subjectDaoImpl.insertEntity(subject);
                idDic.put(subjectoid, subject.getId());
                // 2.?????????
                for (SubjectVersion version : bean.getSubjectVersions()) {
                    if (version.getSubjectid().equals(subjectoid)) {
                        String versionoid = version.getId();
                        version.setId(null);
                        version.setCuser(user.getId());
                        version.setCusername(user.getName());
                        version.setSubjectid(subject.getId());
                        version.setTipnote(raplaceWtspFileId(
                                version.getTipnote(), fileIdDic, "?????????"));
                        version = subjectversionDaoImpl.insertEntity(version);
                        idDic.put(versionoid, version.getId());
                        // 3.?????????
                        for (SubjectAnswer answer : bean.getSubjectAnswers()) {
                            if (answer.getVersionid().equals(versionoid)) {
                                String answeroid = answer.getId();
                                answer.setCuser(user.getId());
                                answer.setCusername(user.getName());
                                answer.setVersionid(version.getId());
                                answer.setAnswernote(raplaceWtspFileId(
                                        answer.getAnswernote(), fileIdDic,
                                        "?????????"));
                                answer = subjectanswerDaoImpl
                                        .insertEntity(answer);
                                idDic.put(answeroid, answer.getId());
                            }
                        }
                    }
                }
                // 4.?????????
                for (SubjectAnalysis analysis : bean.getSubjectAnalysies()) {
                    if (analysis.getSubjectid().equals(subjectoid)) {
                        String oid = analysis.getId();
                        analysis.setCuser(user.getId());
                        analysis.setCusername(user.getName());
                        analysis.setSubjectid(subject.getId());
                        analysis.setText(raplaceWtspFileId(analysis.getText(),
                                fileIdDic, "?????????"));
                        analysis = SubjectAnalysisDaoImpl
                                .insertEntity(analysis);
                        idDic.put(oid, analysis.getId());
                    }
                }
                subject.setVersionid(idDic.get(subject.getVersionid()));
                subjectDaoImpl.editEntity(subject);
            }
        }
        // // --??????3??????
        // 1.??????
        Paper paper = bean.getPaper();
        {
            {
                String oid = paper.getId();
                paper.setExamtypeid(examTypeId);
                paper.setCuser(user.getId());
                paper.setCusername(user.getName());
                paper.setEuser(user.getId());
                paper.setEusername(user.getName());
                paper.setCompletetnum(0);
                paper.setBooknum(0);
                paper.setAvgpoint(0);
                paper.setToppoint(0);
                paper.setLowpoint(0);
                paper.setPapernote(raplaceWtspFileId(paper.getPapernote(),
                        fileIdDic, "??????"));
                paper = paperDaoImpl.insertEntity(paper);
                idDic.put(oid, paper.getId());
            }
            // 2.????????????
            for (PaperChapter chapter : bean.getChapters()) {
                String oid = chapter.getId();
                chapter.setTextnote(raplaceWtspFileId(chapter.getTextnote(),
                        fileIdDic, "????????????"));
                chapter.setPaperid(idDic.get(chapter.getPaperid()));
                chapter = paperchapterDaoImpl.insertEntity(chapter);
                idDic.put(oid, chapter.getId());
                chapterIdDic.put(oid, chapter.getId());
            }
            // 3.?????????
            for (PaperSubject psubject : bean.getPaperSubjects()) {
                String oid = psubject.getId();
                psubject.setVersionid(idDic.get(psubject.getVersionid()));
                psubject.setSubjectid(idDic.get(psubject.getSubjectid()));
                psubject.setChapterid(idDic.get(psubject.getChapterid()));
                psubject.setPaperid(idDic.get(psubject.getPaperid()));
                psubject = papersubjectDaoImpl.insertEntity(psubject);
                idDic.put(oid, psubject.getId());
            }
            // ????????????????????????parentid???treeid??????????????????id??????????????????????????????????????????
            {
                // ??????chapters:parentid???treecode
                for (PaperChapter chapter : bean.getChapters()) {
                    chapter = paperchapterDaoImpl.getEntity(chapter.getId());
                    String parentid = chapterIdDic.get(chapter.getParentid());
                    if (StringUtils.isBlank(parentid)) {
                        chapter.setParentid("NONE");
                    } else {
                        chapter.setParentid(parentid);
                    }
                    String treecode = chapter.getTreecode();
                    for (Entry<String, String> entry : chapterIdDic.entrySet()) {
                        treecode = treecode.replace(entry.getKey(),
                                entry.getValue());
                    }
                    chapter.setTreecode(treecode);
                    paperchapterDaoImpl.editEntity(chapter);
                }
            }
        }
        {// ??????????????????
            for (Entry<String, String> file : fileIdDic.entrySet()) {
                String fileid = file.getValue();
                Docfile dfile = farmFileManagerImpl.getFile(fileid);
                if (dfile.getPstate().equals("0")) {
                    farmFileManagerImpl.delFile(fileid, user);
                }
            }
        }
        return paper.getId();
    }

    /**
     * ??????wtfp??????????????????????????????id
     *
     * @param htmltext
     * @param fileIdDic
     * @return
     */
    private String raplaceWtspFileId(String htmltext,
                                     Map<String, String> fileIdDic, String filenote) {
        if (htmltext != null) {
            for (Entry<String, String> iddic : fileIdDic.entrySet()) {
                if (htmltext.indexOf(iddic.getKey()) >= 0) {
                    htmltext = htmltext.replaceAll(iddic.getKey(),
                            iddic.getValue());
                    farmFileManagerImpl.submitFile(iddic.getValue(), "wtsp??????"
                            + filenote);
                }
            }
        }
        return htmltext;
    }

    @Override
    @Transactional
    public List<String> getAllSubjectVersionids(String paperid) {
        return papersubjectDaoImpl.getAllSubjectVersionids(paperid);
    }
}
