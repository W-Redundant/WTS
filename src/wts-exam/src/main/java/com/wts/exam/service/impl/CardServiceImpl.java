package com.wts.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBSort;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.sql.query.DataQuerys;
import com.farm.core.sql.result.DataResult;
import com.farm.core.sql.result.ResultsHandle;
import com.farm.core.time.TimeTool;
import com.wts.exam.domain.*;
import com.wts.exam.domain.ex.*;
import com.wts.exam.mapper.*;
import com.wts.exam.service.*;
import com.wts.exam.utils.CardPointAutoCounter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/* *
 *功能：答题卡服务层实现类
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@Service
@Slf4j
public class CardServiceImpl implements CardServiceInter {

    @Autowired
    private CardAnswerMapper cardAnswerMapper;
    @Autowired
    private PaperSubjectMapper paperSubjectMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private RoomPaperMapper roomPaperMapper;
    @Autowired
    private RoomUserMapper roomUserMapper;
    @Autowired
    private CardPointMapper cardPointMapper;
    @Autowired
    private PaperMapper paperMapper;
    @Autowired
    private PaperUserownMapper paperUserownMapper;

    @Autowired
    private PaperUserOwnServiceInter paperUserOwnServiceImpl;
    @Autowired
    private ExamStatServiceInter examStatServiceImpl;
    @Autowired
    private SubjectServiceInter subjectServiceImpl;
    @Autowired
    private ExamPopsServiceInter examPopsServiceImpl;
    @Autowired
    private RoomServiceInter roomServiceImpl;

    @Override
    @Transactional
    public Card editCardEntity(Card entity) {
        Card entity2 = cardMapper.getEntity(entity.getId());
        entity2.setPcontent(entity.getPcontent());
        entity2.setEndtime(entity.getEndtime());
        entity2.setPstate(entity.getPstate());
        entity2.setStarttime(entity.getStarttime());
        entity2.setAdjudgetime(entity.getAdjudgetime());
        entity2.setAdjudgeuser(entity.getAdjudgeuser());
        entity2.setPoint(entity.getPoint());
        entity2.setUserid(entity.getUserid());
        entity2.setRoomid(entity.getRoomid());
        entity2.setPaperid(entity.getPaperid());
        entity2.setId(entity.getId());
        cardMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteCardEntity(String id, LoginUser user) {
        // WTS_CARD_ANSWER
        cardAnswerMapper.deleteByCardId(id);
        // WTS_CARD_POINT
        cardPointMapper.deleteByCardId(id);
        // 删除用户答卷记录
        paperUserownMapper.deleteByCardId(id);

        cardMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public Card getCardEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return cardMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createCardSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_CARD a left join alone_auth_user b on a.USERID=b.ID",
                        "a.ID as ID,a.USERID as USERID,b.name as USERNAME,a.STARTTIME as STARTTIME,a.ENDTIME as ENDTIME,a.POINT as POINT,a.ADJUDGETIME as ADJUDGETIME,a.ADJUDGEUSERNAME as ADJUDGEUSERNAME,a.PSTATE as PSTATE");
        return dbQuery;
    }

    @Override
    public DataQuery createRoomCardQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_ROOM_PAPER p left join  WTS_CARD a on p.ROOMID=a.ROOMID and p.PAPERID=a.PAPERID left join alone_auth_user b on a.USERID=b.ID",
                        "a.ID as ID,a.USERID as USERID,a.COMPLETENUM as COMPLETENUM,a.ALLNUM as ALLNUM,b.name as USERNAME,a.STARTTIME as STARTTIME,a.ENDTIME as ENDTIME,a.POINT as POINT,a.ADJUDGETIME as ADJUDGETIME,a.ADJUDGEUSERNAME as ADJUDGEUSERNAME,a.PSTATE as PSTATE");
        dbQuery.setDistinct(true);
        return dbQuery;
    }

    @Override
    @Transactional
    public Card creatOrGetCard(String paperid, String roomId,
                               LoginUser currentUser) {
        // 房间是否超时，是否有答题权限
        Room room = roomServiceImpl.getRoomEntity(roomId);
        if (!roomServiceImpl.isLiveTimeRoom(room)) {
            throw new RuntimeException("该房间不可用，未到答题时间!");
        }
        if (currentUser == null) {
            throw new RuntimeException("请先登陆系统!");
        }
        if (!roomServiceImpl.isUserAbleRoom(room.getId(), currentUser)) {
            throw new RuntimeException("当前用户无进入权限!");
        }

        Paper paper = paperMapper.getEntity(paperid);
        if (paper == null) {
            throw new RuntimeException("该试卷不存在!id:" + paperid);
        }
        List<Card> cards = cardMapper.findByPaperIdAndUserIdAndRoomId(paperid, currentUser.getId(), roomId);

        // 查询答题卡
        if (cards.size() > 0) {
            Card card = cards.get(0);
            String endTime = card.getEndtime();
            if (TimeTool.getTimeDate14().compareTo(endTime) >= 0) {
                card.setPstate("3");
                cardMapper.editEntity(card);
            }
            return card;
        } else {// 创建答题卡
            String startTime = null;
            String endTime = null;
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                startTime = TimeTool.getTimeDate14();
                Date date;
                try {
                    date = format.parse(startTime);
                } catch (ParseException e) {
                    throw new RuntimeException("时间格式化错误！");
                }
                Date endtime = TimeTool.getTimeDate12ForMinute(
                        room.getTimelen(), date);
                // 1永久/2限时
                if (room.getTimetype().equals("2")
                        && StringUtils.isNotBlank(room.getEndtime())) {
                    // 2020-07-02 10:00
                    try {
                        Date room_endtime = TimeTool.parseDate(room
                                .getEndtime().replace(" ", "").replace("-", "")
                                .replace(":", ""), "yyyyMMddHHmm");
                        if (room_endtime.compareTo(endtime) < 0) {
                            endtime = room_endtime;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                endTime = format.format(endtime);
            }
            Card card = new Card();
            {// card.setAdjudgetime(adjudgetime);
                // card.setAdjudgeuser(adjudgeuser);
                card.setStarttime(startTime);
                card.setEndtime(endTime);
                card.setPaperid(paperid);
                // card.setPcontent(pcontent);
                card.setPoint((float) 0);
                card.setPstate("1");
                card.setRoomid(roomId);
                card.setOvertime("0");
                card.setUserid(currentUser.getId());
                cardMapper.insertEntity(card);
            }
            {
                // 记录用户答卷记录(匿名房间不记录)
                if (!room.getWritetype().equals("2")) {
                    paperUserOwnServiceImpl.addDoPaperInfo(card.getId(),
                            currentUser);
                    // 計數
                    examStatServiceImpl.addStartCardNum(card.getId(),
                            currentUser);
                }
            }
            return card;
        }
    }

    @Override
    @Transactional
    public Card loadCard(String paperid, String roomid, String userid) {
        List<Card> cards = cardMapper.findByPaperIdAndUserIdAndRoomId(paperid, userid, roomid);
        if (cards.size() > 0) {
            Card card = cards.get(0);
            String endTime = card.getEndtime();
            if (TimeTool.getTimeDate14().compareTo(endTime) >= 0
                    && card.getPstate().equals("1")) {
                // 把未交卷的答题卡设置为超时未交卷
                card.setPstate("3");
                cardMapper.editEntity(card);
            }
            return cards.get(0);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean saveCardVal(Card card, String versionid, String answerid,
                               String value) {
        // 删除已有的答案
        if (card == null) {
            throw new RuntimeException("答题卡获取失败：答题卡");
        }
        // 先刪除历史答案
        cardAnswerMapper.deleteByCardIdAndVersionIdAndAnswerId(card.getId(), versionid, answerid);

        List<CardAnswer> answers = cardAnswerMapper.findByCardIdAndVersionId(card.getId(), versionid);

        // 添加新的答案
        CardAnswer answer = new CardAnswer();
        answer.setAnswerid(answerid);
        answer.setCardid(card.getId());
        answer.setCtime(TimeTool.getTimeDate14());
        answer.setCuser(card.getUserid());
        answer.setPstate("1");
        answer.setValstr(value);
        answer.setVersionid(versionid);
        cardAnswerMapper.insertEntity(answer);
        answers.add(answer);
        // ---------------------------------------------
        SubjectVersion version = subjectServiceImpl.getSubjectUnit(versionid)
                .getVersion();
        TipType tiptype = TipType.getTipType(version.getTiptype());
        return tiptype.getHandle().isHaveAnswer(answers);
    }

    @Override
    @Transactional
    public float getCardPointSum(Card card) {
        List<CardPoint> cardSubs = cardPointMapper.findByCardId(card.getId());

        float point = 0;
        for (CardPoint cardSub : cardSubs) {
            point = point
                    + (cardSub.getPoint() == null ? 0 : cardSub.getPoint());
        }
        return point;
    }

    @Override
    @Transactional
    public int countSubjectPoint(SubjectUnit subjectUnit) {
        int pointWeight = Math.abs(subjectUnit.getTipType().getHandle()
                .runPointWeight(subjectUnit));
        if (pointWeight > 100) {
            pointWeight = 100;
        }
        return pointWeight;
    }

    @Override
    @Transactional
    public boolean runPointCount(List<SubjectUnit> userSubjects, Card card) {
        /**
         * 之前是判断是否存在题得分，如果存在则更新不存在就创建，现在是直接删除再创建不知道有没有问题（原来的逻辑在本方法下方注释掉，需要时回复）
         **/

        Map<String, Integer> points = new HashMap<>();
        boolean isAllComplete = true;
        {
            // 先获得试卷得分得配置
            List<PaperSubject> standPoints = paperSubjectMapper.findByPaperId(card.getPaperid());

            for (PaperSubject standPoint : standPoints) {
                points.put(standPoint.getVersionid(), standPoint.getPoint());
            }
        }
        for (SubjectUnit unit : userSubjects) {
            // 题的得分权重，可以得分得百分比（填空题和问答题都是返回百分比得）
            int pointWeight = countSubjectPoint(unit);
            Integer basePoint = points.get(unit.getVersion().getId());
            int point = (basePoint == null ? 0 : basePoint) * pointWeight / 100;
            cardPointMapper.deleteByCardIdAndVersionId(unit.getCardSubject().getCardid(), unit.getVersion().getId());

            {
                CardPoint cardsub = null;
                cardsub = new CardPoint();
                cardsub.setMpoint(basePoint);
                cardsub.setCardid(unit.getCardSubject().getCardid());
                cardsub.setVersionid(unit.getVersion().getId());
                if (unit.getTipType().equals(TipType.CheckBox)
                        || unit.getTipType().equals(TipType.Select)
                        || unit.getTipType().equals(TipType.Judge)) {
                    cardsub.setComplete("1");
                } else {
                    cardsub.setComplete("0");
                    isAllComplete = false;
                }
                cardsub.setPoint(point);
                cardPointMapper.insertEntity(cardsub);
            }
        }
        return isAllComplete;
    }

    // @Override
    // @Transactional
    // public boolean runPointCount(List<SubjectUnit> userSubjects, Card card) {
    // Map<String, Integer> points = new HashMap<>();
    // boolean isAllComplete = true;
    // {
    // // 先获得试卷得分得配置
    // List<PaperSubject> standPoints = papersubjectDaoImpl.selectEntitys(
    // DBRuleList.getInstance().add(new DBRule("PAPERID", card.getPaperid(),
    // "=")).toList());
    // for (PaperSubject standPoint : standPoints) {
    // points.put(standPoint.getVersionid(), standPoint.getPoint());
    // }
    // }
    // for (SubjectUnit unit : userSubjects) {
    // // 题的得分权重，可以得分得百分比（填空题和问答题都是返回百分比得）
    // int pointWeight = countSubjectPoint(unit);
    // Integer basePoint = points.get(unit.getVersion().getId());
    // int point = (basePoint == null ? 0 : basePoint) * pointWeight / 100;
    // List<CardPoint> cardPoints =
    // cardPointDaoImpl.selectEntitys(DBRuleList.getInstance()
    // //
    // .add(new DBRule("CARDID", unit.getCardSubject().getCardid(), "="))
    // //
    // .add(new DBRule("VERSIONID", unit.getVersion().getId(), "=")).toList());
    // if (cardPoints.size() > 1) {
    // throw new RuntimeException("用戶答題卡cardPoints中的，題數量" + cardPoints.size() +
    // "错误:cardid("
    // + unit.getCardSubject().getCardid() + "),versionid(" +
    // unit.getVersion().getId() + ")");
    // }
    // {
    // CardPoint cardsub = null;
    // if (cardPoints.size() == 0) {
    // // 无就创建
    // cardsub = new CardPoint();
    // cardsub.setCardid(unit.getCardSubject().getCardid());
    // cardsub.setVersionid(unit.getVersion().getId());
    // } else {
    // // 有就修改
    // cardsub = cardPointDaoImpl.getEntity(cardPoints.get(0).getId());
    // }
    // if (unit.getTipType().equals(TipType.CheckBox) ||
    // unit.getTipType().equals(TipType.Select)
    // || unit.getTipType().equals(TipType.Judge)) {
    // cardsub.setComplete("1");
    // } else {
    // cardsub.setComplete("0");
    // isAllComplete = false;
    // }
    // cardsub.setPoint(point);
    // if (cardPoints.size() == 0) {
    // cardPointDaoImpl.insertEntity(cardsub);
    // } else {
    // cardPointDaoImpl.editEntity(cardsub);
    // }
    // }
    // }
    // return isAllComplete;
    // }

    @Override
    @Transactional
    public List<SubjectUnit> loadUserSubjects(Card card) {
        List<SubjectVersion> versions = null;
        List<SubjectAnswer> sanswers = null;
        List<CardAnswer> panswers = null;
        // 查詢試題版本
        try {
            DataQuery query = DataQuery
                    .getInstance(
                            1,
                            "SELECT C.ID AS ID, C.CTIME AS CTIME, C.CUSERNAME AS CUSERNAME, C.CUSER AS CUSER, C.PSTATE AS PSTATE, C.PCONTENT AS PCONTENT, C.TIPSTR AS TIPSTR, C.TIPNOTE AS TIPNOTE, C.TIPTYPE AS TIPTYPE, C.SUBJECTID AS SUBJECTID FROM WTS_CARD a LEFT JOIN WTS_PAPER_SUBJECT b ON a.PAPERID = b.PAPERID LEFT JOIN WTS_SUBJECT_VERSION c ON b.VERSIONID = c.ID WHERE a.ID = '"
                                    + card.getId() + "'");
            query.setPagesize(1000);
            query.setDistinct(true);
            versions = query.search().getObjectList(SubjectVersion.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 試題答案
        try {
            DataQuery query = DataQuery
                    .getInstance(
                            1,
                            "SELECT b.ID AS ID, b.CTIME AS CTIME, b.CUSERNAME AS CUSERNAME, b.CUSER AS CUSER, b.PSTATE AS PSTATE, b.PCONTENT AS PCONTENT, b.VERSIONID AS VERSIONID, b.ANSWER AS ANSWER, b.ANSWERNOTE AS ANSWERNOTE, b.SORT AS SORT, b.RIGHTANSWER AS RIGHTANSWER, b.POINTWEIGHT AS POINTWEIGHT FROM WTS_PAPER_SUBJECT a LEFT JOIN WTS_SUBJECT_ANSWER b ON a.VERSIONID = b.VERSIONID WHERE a.PAPERID = '"
                                    + card.getPaperid()
                                    + "' and b.id is not null");
            query.setPagesize(1000);
            sanswers = query.search().getObjectList(SubjectAnswer.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // 答題卡答案
        try {
            DataQuery query = DataQuery
                    .getInstance(
                            1,
                            "select ID,CARDID,ANSWERID,VERSIONID,CUSER,VALSTR,CTIME,PCONTENT,PSTATE from WTS_CARD_ANSWER where CARDID='"
                                    + card.getId() + "'");
            query.setPagesize(1000);
            panswers = query.search().getObjectList(CardAnswer.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // --------------------------------------------------
        List<SubjectUnit> subjects = new ArrayList<>();
        {
            // <题版本id，题对象>
            Map<String, SubjectUnit> subjectMap = new HashMap<>();
            for (SubjectVersion version : versions) {
                if (version != null && version.getId() != null) {
                    // 构建答卷时可能试题已经不存在了
                    // 构造试题字典
                    SubjectUnit unit = new SubjectUnit();
                    unit.setVersion(version);
                    unit.setTipType(TipType.getTipType(version.getTiptype()));
                    unit.setAnswers(new ArrayList<AnswerUnit>());
                    subjectMap.put(version.getId(), unit);
                }
            }
            Map<String, List<CardAnswer>> answerValsMap = new HashMap<>();
            for (CardAnswer answer : panswers) {
                // 構造需填充參數需要用到的Map<String, List<CardAnswer>>
                List<CardAnswer> answers = answerValsMap.get(answer
                        .getVersionid());
                if (answers == null) {
                    answers = new ArrayList<>();
                    answerValsMap.put(answer.getVersionid(), answers);
                }
                answers.add(answer);
            }
            for (SubjectAnswer answer : sanswers) {
                // 將題選項填充到題對象中
                SubjectUnit unit = subjectMap.get(answer.getVersionid());
                AnswerUnit answerUnit = new AnswerUnit();
                answerUnit.setAnswer(answer);
                if (unit == null) {
                    System.out.println("this subject is null:"
                            + answer.getVersionid());
                } else {
                    unit.getAnswers().add(answerUnit);
                }
            }
            for (Entry<String, SubjectUnit> entry : subjectMap.entrySet()) {
                // 构造用户题和答案得封装
                SubjectUnit unit = entry.getValue();
                {
                    CardPoint cardsub = new CardPoint();
                    cardsub.setCardid(card.getId());
                    cardsub.setComplete("0");
                    cardsub.setPoint(0);
                    cardsub.setVersionid(unit.getVersion().getId());
                    unit.setCardSubject(cardsub);
                }
                List<CardAnswer> panswerList = answerValsMap.get(unit
                        .getVersion().getId());
                if (unit == null || panswerList == null) {
                    continue;
                }
                TipType.getTipType(unit.getVersion().getTiptype()).getHandle()
                        .loadVal(unit, panswerList);
                subjects.add(entry.getValue());
            }
        }
        return subjects;
    }

    @Override
    @Transactional
    public void loadCardPoint(PaperUnit paper, Card card) {
        // 取得所有答题卡的值
        List<CardPoint> answerPoints = cardPointMapper.findByCardId(card.getId());

        if (answerPoints.size() <= 0) {
            return;
        }
        Map<String, CardPoint> pointMap = new HashMap<>();
        for (CardPoint paperSub : answerPoints) {
            pointMap.put(paperSub.getVersionid(), paperSub);
        }
        // 填充道试卷中
        for (ChapterUnit chapter1 : paper.getChapters()) {
            // 第一層章節
            List<SubjectUnit> subjects = chapter1.getSubjects();
            loadSubjectPoint(subjects, pointMap);
            for (ChapterUnit chapter2 : chapter1.getChapters()) {
                // 第二層章節
                List<SubjectUnit> subjects2 = chapter2.getSubjects();
                loadSubjectPoint(subjects2, pointMap);
                for (ChapterUnit chapter3 : chapter2.getChapters()) {
                    // 第三層章節
                    List<SubjectUnit> subjects3 = chapter3.getSubjects();
                    loadSubjectPoint(subjects3, pointMap);
                }
            }
        }
    }

    /**
     * 将考题值填充到题目中
     *
     * @param subjects   试题序列
     * @param answerVals map<试题版本id,用户答案序列>
     */
    private void loadSubjectPoint(List<SubjectUnit> subjects,
                                  Map<String, CardPoint> pointMap) {
        for (SubjectUnit nuit : subjects) {
            CardPoint point = pointMap.get(nuit.getVersion().getId());
            if (point == null) {
                point = new CardPoint();
            }
            nuit.setCardSubject(point);
        }
    }

    @Override
    @Transactional
    public void loadCardVal(PaperUnit paper, Card card) {
        // 取得所有答题卡的值
        List<CardAnswer> answerVals = cardAnswerMapper.findByCardId(card.getId());

        if (answerVals.size() <= 0) {
            return;
        }
        Map<String, List<CardAnswer>> answerValsMap = new HashMap<>();
        for (CardAnswer answer : answerVals) {
            List<CardAnswer> panswers = answerValsMap
                    .get(answer.getVersionid());
            if (panswers == null) {
                panswers = new ArrayList<>();
                answerValsMap.put(answer.getVersionid(), panswers);
            }
            panswers.add(answer);
        }
        // 填充道试卷中
        for (ChapterUnit chapter1 : paper.getChapters()) {
            // 第一層章節
            List<SubjectUnit> subjects = chapter1.getSubjects();
            int valNum1 = loadSubjectVal(subjects, answerValsMap);
            for (ChapterUnit chapter2 : chapter1.getChapters()) {
                // 第二層章節
                List<SubjectUnit> subjects2 = chapter2.getSubjects();
                int valNum2 = loadSubjectVal(subjects2, answerValsMap);
                for (ChapterUnit chapter3 : chapter2.getChapters()) {
                    // 第三層章節
                    List<SubjectUnit> subjects3 = chapter3.getSubjects();
                    int valNum3 = loadSubjectVal(subjects3, answerValsMap);
                    valNum2 = valNum2 + valNum3;
                    chapter3.setVasNum(valNum3);
                }
                valNum1 = valNum1 + valNum2;
                chapter2.setVasNum(valNum2);
            }
            chapter1.setVasNum(valNum1);
        }
    }

    /**
     * 将考题值填充到题目中
     *
     * @param subjects   试题序列
     * @param answerVals map<试题版本id,用户答案序列>
     * @return 用戶答題數量
     */
    private int loadSubjectVal(List<SubjectUnit> subjects,
                               Map<String, List<CardAnswer>> answerVals) {
        int valNum = 0;
        for (SubjectUnit nuit : subjects) {
            List<CardAnswer> answers = answerVals
                    .get(nuit.getVersion().getId());
            if (answers == null) {
                answers = new ArrayList<>();
            }
            nuit.getTipType().getHandle().loadVal(nuit, answers);
            if (nuit.isFinishIs()) {
                valNum++;
            }
        }
        return valNum;
    }

    @Override
    @Transactional
    public boolean isTheTimeAble(Card card) {
        String endTime = card.getEndtime();
        if (TimeTool.getTimeDate14().compareTo(endTime) >= 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    @Transactional
    public DataResult getRoomPaperUsers(final String roomId,
                                        final String paperid, DataQuery query) {
        final Room room = roomMapper.getEntity(roomId);
        // 用户名称，得分，判卷人，判卷时间，答题开始时间，答题交卷时间，状态
        if (StringUtils.isNotBlank(paperid)) {
            DataQuerys.wipeVirus(paperid);
        }
        DataQuery thisQuery = null;
        if (room.getWritetype().equals("1")) {
            String paperRule = "";
            if (StringUtils.isNotBlank(paperid)) {
                paperRule = "and B.PAPERID = '" + paperid + "'";
            }
            // /1指定人
            thisQuery = DataQuery
                    .init(query,
                            "WTS_ROOM_USER a LEFT JOIN WTS_CARD b ON a.USERID = b.USERID AND a.ROOMID = b.ROOMID "
                                    + paperRule
                                    + "  LEFT JOIN alone_auth_user c ON c.ID = a.USERID LEFT JOIN WTS_PAPER d on d.id=b.PAPERID",
                            "c. NAME AS NAME,d.NAME as PAPERNAME,d.id as PAPERID,b.OVERTIME as OVERTIME,b.COMPLETENUM AS COMPLETENUM,b.ALLNUM as ALLNUM,c.ID AS USERID,B.ID as CARDID, b.POINT AS point, b.ADJUDGEUSERNAME AS ADJUDGEUSERNAME, b.ADJUDGETIME AS ADJUDGETIME, b.STARTTIME AS STARTTIME, b.ENDTIME AS endtime, b.PSTATE AS PSTATE, b.PSTATE AS PSTATETITLE");
            thisQuery.addRule(new DBRule("A.ROOMID", roomId, "="));
        }
        if (room.getWritetype().equals("0")) {
            // 0任何人
            thisQuery = DataQuery
                    .init(query,
                            "WTS_CARD b  LEFT JOIN alone_auth_user c ON c.ID = b.USERID LEFT JOIN WTS_PAPER d on d.id=b.PAPERID",
                            "c. NAME AS NAME,b.COMPLETENUM AS COMPLETENUM,d.NAME as PAPERNAME,b.OVERTIME as OVERTIME,d.id as PAPERID,b.ALLNUM as ALLNUM,c.ID AS USERID,B.ID as CARDID, b.POINT AS point, b.ADJUDGEUSERNAME AS ADJUDGEUSERNAME, b.ADJUDGETIME AS ADJUDGETIME, b.STARTTIME AS STARTTIME, b.ENDTIME AS endtime, b.PSTATE AS PSTATE, b.PSTATE AS PSTATETITLE");
            if (StringUtils.isNotBlank(paperid)) {
                thisQuery.addRule(new DBRule("B.PAPERID", paperid, "="));
            }
            thisQuery.addRule(new DBRule("B.ROOMID", roomId, "="));
        }
        if (room.getWritetype().equals("2")) {
            // 2匿名用戶（userid是一個伪id）
            thisQuery = DataQuery
                    .init(query,
                            "WTS_CARD b LEFT JOIN WTS_PAPER d on d.id=b.PAPERID",
                            "b.USERID AS NAME,b.COMPLETENUM AS COMPLETENUM,d.NAME as PAPERNAME,b.OVERTIME as OVERTIME,d.id as PAPERID,b.ALLNUM as ALLNUM,B.USERID AS USERID,B.ID as CARDID, b.POINT AS point, b.ADJUDGEUSERNAME AS ADJUDGEUSERNAME, b.ADJUDGETIME AS ADJUDGETIME, b.STARTTIME AS STARTTIME, b.ENDTIME AS ENDTIME, b.PSTATE AS PSTATE, b.PSTATE AS PSTATETITLE");
            if (StringUtils.isNotBlank(paperid)) {
                thisQuery.addRule(new DBRule("B.PAPERID", paperid, "="));
            }
            thisQuery.addRule(new DBRule("B.ROOMID", roomId, "="));
        }
        try {
            thisQuery.addDefaultSort(new DBSort("B.STARTTIME", "ASC"));
            DataResult roomPaperUsers = thisQuery.search();
            roomPaperUsers.runHandle(new ResultsHandle() {
                @Override
                public void handle(Map<String, Object> row) {
                    // 处理答题且超时的答题卡，设置未超时未提交的状态
                    if (row.get("PSTATE") != null
                            && row.get("PSTATE").equals("1")
                            && StringUtils.isNotBlank((String) row
                            .get("USERID"))) {
                        Card card = null;
                        if (StringUtils.isNotBlank(paperid)) {
                            card = loadCard(paperid, roomId,
                                    (String) row.get("USERID"));
                        }
                        if (StringUtils.isNotBlank((String) row.get("CARDID"))) {
                            card = getCardEntity((String) row.get("CARDID"));
                        }
                        if (card != null) {
                            row.put("PSTATE", card.getPstate());
                            row.put("PSTATETITLE", card.getPstate());
                        }
                    }
                    // 处理匿名用户的名称
                    if (room.getWritetype().equals("2")) {
                        row.put("NAME", ((String) row.get("NAME")).replaceAll(
                                "ANONYMOUS", "匿名"));
                    }
                }
            });
            return roomPaperUsers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public CardInfo autoCountCardPoint(String cardid) {
        Card card = getCardEntity(cardid);
        Room room = roomMapper.getEntity(card.getRoomid());
        List<SubjectUnit> subjects = loadUserSubjects(card);
        // 此处主要慢
        boolean isAllComplete = runPointCount(subjects, card);
        // 如果题目全部判完，就把考卷状态设置为已经阅卷完成的状态
        if (isAllComplete) {
            // 1:开始答题,2:手动交卷,3:超时未交卷,4:超时自动交卷,5:已自动阅卷,6:已完成阅卷,7:发布成绩
            if (room != null && room.getCounttype() != null
                    && room.getCounttype().equals("2")) {
                card.setPstate("7");
            } else {
                card.setPstate("5");
            }
            card.setAdjudgetime(TimeTool.getTimeDate14());
            card.setAdjudgeusername("自动");
        } else {
            card.setPstate("5");
        }
        card.setPoint(getCardPointSum(card));
        // ------------------統計用戶答題數量-----------------------
        CardInfo info = new CardInfo();
        {
            int completeNum = 0;
            info.setAllNum(paperSubjectMapper.countByPaperId(card.getPaperid()));
            for (SubjectUnit subject : subjects) {
                if (subject.isFinishIs()) {
                    completeNum++;
                }
            }
            info.setCompleteNum(completeNum);
        }
        card.setCompletenum(info.getCompleteNum());
        card.setAllnum(info.getAllNum());
        editCardEntity(card);
        return info;
    }

    @Override
    @Transactional
    public void finishAdjudge(Card card, Map<String, Integer> points,
                              LoginUser currentUser) {
        List<CardPoint> cardPoints = cardPointMapper.findByCardId(card.getId());

        for (CardPoint cardSub : cardPoints) {
            Integer point = points.get(cardSub.getVersionid());
            if (point != null) {
                CardPoint cardSubEntity = cardPointMapper.getEntity(cardSub.getId());
                cardSubEntity.setPoint(point);
                // cardSubEntity.setComplete("1");
                cardPointMapper.editEntity(cardSubEntity);
            }
        }
        card = getCardEntity(card.getId());
        card.setAdjudgetime(TimeTool.getTimeDate14());
        card.setAdjudgeuser(currentUser.getId());
        card.setAdjudgeusername(currentUser.getName());
        card.setPoint(getCardPointSum(card));
        // 1:开始答题,2:手动交卷,3:超时未交卷,4:超时自动交卷,5:已自动阅卷,6:已完成阅卷,7:发布成绩
        card.setPstate("6");
        editCardEntity(card);
    }

    @Override
    @Transactional
    public void publicPoint(String cardid, LoginUser currentUser) {
        Card card = getCardEntity(cardid);
        if (card.getPstate().equals("5") || card.getPstate().equals("6")) {
            // 1:开始答题,2:手动交卷,3:超时未交卷,4:超时自动交卷,5:已自动阅卷,6:已完成阅卷,7:发布成绩
            card.setPstate("7");
            editCardEntity(card);
            paperUserOwnServiceImpl.refreshScore(cardid, card.getPoint());
        }
    }

    @Override
    @Transactional
    public void loadPaperUserNum(RoomUnit roomunit) {
        // 查找考場的指定人數
        for (PaperUnit paper : roomunit.getPapers()) {
            PaperUnit paperUnit = getRoomPaperUserNums(roomunit.getRoom()
                    .getId(), paper.getInfo().getId());
            if (roomunit.getRoom().getWritetype().equals("1")) {
                paper.setAllUserNum(paperUnit.getAllUserNum());
            }
            paper.setCurrentUserNum(paperUnit.getCurrentUserNum());
            paper.setAdjudgeUserNum(paperUnit.getAdjudgeUserNum());
        }
    }

    @Override
    @Transactional
    public PaperUnit getRoomPaperUserNums(String roomid, String paperid) {
        PaperUnit paperUnit = new PaperUnit();
        int currentUsers = cardMapper.countByPaperIdAndRoomId(paperid, roomid);

        int adjudgeUserNum1 = cardMapper.countByPaperIdAndRoomIdAndPstate(paperid, roomid, "5");
        int adjudgeUserNum2 = cardMapper.countByPaperIdAndRoomIdAndPstate(paperid, roomid, "6");
        int adjudgeUserNum3 = cardMapper.countByPaperIdAndRoomIdAndPstate(paperid, roomid, "7");
        int allUser = roomUserMapper.countByRoomId(roomid);
        paperUnit.setCurrentUserNum(currentUsers);
        paperUnit.setAdjudgeUserNum(adjudgeUserNum1 + adjudgeUserNum2
                + adjudgeUserNum3);
        paperUnit.setAllUserNum(allUser);
        return paperUnit;
    }

    @Override
    @Transactional
    public void clearRoomCard(String roomid, String paperid,
                              LoginUser currentUser) {
        List<Card> cards = cardMapper.findByPaperIdAndRoomId(paperid, roomid);
        for (Card card : cards) {
            deleteCardEntity(card.getId(), currentUser);
        }
    }

    @Override
    @Transactional
    public void clearPaperUserCard(String roomid, String paperid, LoginUser user) {
        if (user == null) {
            throw new RuntimeException("无法获得操作用户!");
        }
        List<Card> cards = cardMapper.findByPaperIdAndUserIdAndRoomId(paperid, user.getId(), roomid);
        if (cards.size() > 1) {
            throw new RuntimeException("the cards is many card!");
        } else {
            for (Card card : cards) {
                deleteCardEntity(card.getId(), user);
            }
        }
    }

    @Override
    @Transactional
    public void deleteCardsByRoom(String roomid, LoginUser user) {
        List<Card> cards = cardMapper.findByRoomId("roomid");

        for (Card card : cards) {
            // 删除答题卡用户答案
            cardAnswerMapper.deleteByCardId(card.getId());
            // 删除答卷试题得分
            cardPointMapper.deleteByCardId(card.getId());
            // 删除答题卡
            cardMapper.deleteEntity(card.getId());
        }
    }

    @Override
    @Transactional
    public List<String> getUserPaperidsByRoom(String roomid, String userid) {
        Set<String> roompaperIds = new HashSet<>();
        {
            // 房间当前有的答卷（房间有可能有历史答卷，用户之前答卷但是后来被从房间删除了）
            List<RoomPaper> papers = roomPaperMapper.findByRoomId(roomid);

            for (RoomPaper paper : papers) {
                roompaperIds.add(paper.getPaperid());
            }
        }
        List<Card> list = cardMapper.findByRoomIdAndUserId(roomid, userid);

        List<String> ids = new ArrayList<>();
        for (Card card : list) {
            if (roompaperIds.contains(card.getPaperid())) {
                // 只传出当前房间有的答卷
                ids.add(card.getPaperid());
            }
        }
        return ids;
    }

    @Override
    @Transactional
    public DataResult getRoomUsers(String roomId, DataQuery query) {
        return getRoomPaperUsers(roomId, null, query);
    }

    @Override
    @Transactional
    public List<Card> getRoomCards(String roomid) {
        List<Card> list = cardMapper.findByRoomId(roomid);
        return list;
    }

    @Override
    @Transactional
    public void finishExam(String cardId, LoginUser currentUser) {
        Card card = cardMapper.getEntity(cardId);
        {
            // 数据校验
            if (currentUser == null) {
                throw new RuntimeException("未登陆当前用户!");
            }
            if (!card.getUserid().equals(currentUser.getId())
                    && examPopsServiceImpl.isNotJudger(card.getRoomid(),
                    currentUser)) {
                throw new RuntimeException("答题卡用户非当前用户和判卷人!");
            }
            if (!card.getPstate().equals("1") && !card.getPstate().equals("3")) {
                // 非答题状态，不用交卷
                return;
            }
        }
        finishExamNoPop(cardId, currentUser);
    }

    @Override
    @Transactional
    public void finishExamNoPop(String cardid, LoginUser currentUser) {
        Card card = cardMapper.getEntity(cardid);
        if (isTheTimeAble(card)) {
            // 在答题时间内，手动交卷
            card.setPstate("2");
            card.setOvertime("1");
            card.setEndtime(TimeTool.getTimeDate14());
        } else {
            // 非答题时间内，自动强制交卷
            card.setPstate("4");
            card.setOvertime("2");
        }
        cardMapper.editEntity(card);
        {// 计算得分(回填用戶完成題的數量)
            // autoCountCardPoint(cardid, currentUser);
            // 改为异步计算
            CardPointAutoCounter.submitTask(cardid);
        }
    }

    @Override
    @Transactional
    public void clearRoomCard(String roomid, LoginUser currentUser) {
        List<Card> cards = cardMapper.findByRoomId(roomid);
        for (Card card : cards) {
            deleteCardEntity(card.getId(), currentUser);
        }
    }

    @Override
    @Transactional
    public void reAdjudge(String cardid, LoginUser currentUser) {
        Card card = cardMapper.getEntity(cardid);
        card.setPstate("2");
        cardMapper.editEntity(card);
    }

    @Override
    @Transactional
    public int getCardCompleteNum(String cardid) {
        try {
            DataQuery query = DataQuery.getInstance(1,
                    "select VERSIONID,CUSER from WTS_CARD_ANSWER where CARDID='"
                            + cardid + "'");
            query.setPagesize(1000);
            query.setNoCount();
            query.setDistinct(true);
            return query.search().getResultList().size();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
