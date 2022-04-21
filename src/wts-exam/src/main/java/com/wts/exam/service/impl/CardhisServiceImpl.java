package com.wts.exam.service.impl;

import com.farm.authority.FarmAuthorityService;
import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DBSort;
import com.farm.core.sql.query.DataQuery;
import com.wts.exam.dao.CardDaoInter;
import com.wts.exam.dao.CardPointHisDaoInter;
import com.wts.exam.dao.PaperDaoInter;
import com.wts.exam.dao.RoomDaoInter;
import com.wts.exam.domain.*;
import com.wts.exam.mapper.*;
import com.wts.exam.service.CardHisServiceInter;
import com.wts.exam.service.RoomPaperServiceInter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class CardhisServiceImpl implements CardHisServiceInter {

    @Autowired
    private CardHisMapper cardHisMapper;
    @Autowired
    private CardAnswerMapper cardAnswerMapper;
    @Autowired
    private CardAnswerHisMapper cardAnswerHisMapper;
    @Autowired
    private CardPointMapper cardPointMapper;
    @Autowired
    private CardPointHisMapper cardPointHisMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private CardMapper cardMapper;
    @Autowired
    private PaperMapper paperMapper;

    @Autowired
    RoomPaperServiceInter roomPaperServiceImpl;

    @Override
    @Transactional
    public CardHis insertCardhisEntity(CardHis entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        // entity.setCuser(user.getId());
        // entity.setCtime(TimeTool.getTimeDate14());
        // entity.setCusername(user.getName());
        // entity.setEuser(user.getId());
        // entity.setEusername(user.getName());
        // entity.setEtime(TimeTool.getTimeDate14());
        // entity.setPstate("1");
        cardHisMapper.insertEntity(entity);
        return entity;
    }

    @Override
    @Transactional
    public CardHis editCardhisEntity(CardHis entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        CardHis entity2 = cardHisMapper.getEntity(entity.getId());
        // entity2.setEuser(user.getId());
        // entity2.setEusername(user.getName());
        // entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setAllnum(entity.getAllnum());
        entity2.setCompletenum(entity.getCompletenum());
        entity2.setEndtime(entity.getEndtime());
        entity2.setPstate(entity.getPstate());
        entity2.setPcontent(entity.getPcontent());
        entity2.setStarttime(entity.getStarttime());
        entity2.setAdjudgetime(entity.getAdjudgetime());
        entity2.setAdjudgeusername(entity.getAdjudgeusername());
        entity2.setAdjudgeuser(entity.getAdjudgeuser());
        entity2.setPoint(entity.getPoint());
        entity2.setUserid(entity.getUserid());
        entity2.setRoomid(entity.getRoomid());
        entity2.setRoomname(entity.getRoomname());
        entity2.setPaperid(entity.getPaperid());
        entity2.setPapername(entity.getPapername());
        entity2.setId(entity.getId());
        cardHisMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteCardhisEntity(String id, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        cardHisMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public CardHis getCardhisEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return cardHisMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createHisQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_CARD_HIS a LEFT JOIN ALONE_AUTH_USERORG b ON b.USERID = a.USERID LEFT JOIN ALONE_AUTH_ORGANIZATION c ON b.ORGANIZATIONID = c.ID",
                        "A.ID AS CARDID,A.PSTATE as PSTATE,A.ROOMNAME AS ROOMNAME,A.USERNAME as USERNAME,A.PAPERNAME AS PAPERNAME,C.NAME AS ORGNAME,A.POINT AS POINT,A.STARTTIME AS STARTTIME");
        dbQuery.addDefaultSort(new DBSort("a.STARTTIME", "desc"));
        return dbQuery;
    }

    @Override
    @Transactional
    public DataQuery createLiveQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "WTS_CARD a LEFT JOIN ALONE_AUTH_USERORG b ON b.USERID = a.USERID LEFT JOIN ALONE_AUTH_ORGANIZATION c ON b.ORGANIZATIONID = c.ID LEFT JOIN WTS_ROOM d ON d.ID = a.ROOMID LEFT JOIN wts_paper e ON e.id = a.PAPERID LEFT JOIN alone_auth_user f ON f.id = a.USERID",
                        "A.ID AS CARDID,A.PSTATE as PSTATE,d. NAME AS ROOMNAME, f. NAME AS USERNAME, e. NAME AS PAPERNAME, C. NAME AS ORGNAME, A.POINT AS POINT, A.STARTTIME AS STARTTIME");
        dbQuery.addDefaultSort(new DBSort("a.STARTTIME", "desc"));
        dbQuery.addSqlRule(" and d.id is not null");
        return dbQuery;
    }

    @Override
    @Transactional
    public void backup(String roomid, LoginUser currentUser) throws Exception {
        List<Card> cards = cardMapper.findByRoomId(roomid);
        // 备份card
        for (Card card : cards) {
            CardHis cardhis = new CardHis();
            BeanUtils.copyProperties(cardhis, card);
            cardhis.setRoomname(roomMapper.getEntity(cardhis.getRoomid())
                    .getName());
            Paper paper = paperMapper.getEntity(cardhis.getPaperid());
            String paperName = null;
            if (paper != null) {
                paperName = paper.getName();
                // 答卷别名
                String paperAlias = roomPaperServiceImpl.getPaperAlias(roomid,
                        paper.getId());
                if (paperAlias != null) {
                    paperName = paperAlias;
                }
            } else {
                // 答卷在归档前有可能被删除
                paperName = "失效答卷";
            }
            cardhis.setPapername(paperName);
            if (FarmAuthorityService.getInstance()
                    .getUserById(card.getUserid()) != null) {
                cardhis.setUsername(FarmAuthorityService.getInstance()
                        .getUserById(card.getUserid()).getName());
            } else {
                cardhis.setUsername("失效用户");
            }
            cardHisMapper.insertEntity(cardhis);
            // 备份card-answer
            if (cardAnswerHisMapper.countEntitys(card.getId()) <= 0) {
                List<CardAnswer> cardAnswer = cardAnswerMapper.findByCardId(card.getId());
                CardAnswerHis cardAnswerHis = new CardAnswerHis();
                BeanUtils.copyProperties(cardAnswerHis, cardAnswer.get(0));
                cardAnswerHisMapper.insertEntity(cardAnswerHis);
            }

            if (cardPointHisMapper.countEntitys(card.getId()) <= 0){
                List<CardPoint> cardPoint = cardPointMapper.findByCardId(card.getId());
                CardPointHis cardPointHis = new CardPointHis();
				BeanUtils.copyProperties(cardPointHis, cardPoint.get(0));
				cardPointHisMapper.insertEntity(cardPointHis);
			}
        }
    }

    @Override
    @Transactional
    public DataQuery createUserCardQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "(( SELECT ID AS CARDID,USERID, PSTATE, ROOMNAME, PAPERNAME, POINT, STARTTIME,'BACK' as SOURCE FROM WTS_CARD_HIS ) UNION ( SELECT a.ID AS CARDID,a.USERID as USERID, a.PSTATE AS PSTATE, b. NAME AS ROOMNAME, c. NAME AS PAPERNAME, a.POINT AS POINT, a.STARTTIME AS STARTTIME,'LIVE' as SOURCE FROM WTS_CARD a LEFT JOIN WTS_ROOM b ON b.ID = a.ROOMID LEFT JOIN wts_paper c ON c.id = a.PAPERID where a.PSTATE='7')) ALLDATA",
                        "CARDID, PSTATE, ROOMNAME,USERID, PAPERNAME, POINT, STARTTIME,SOURCE");
        dbQuery.addDefaultSort(new DBSort("STARTTIME", "desc"));
        return dbQuery;
    }

    @Override
    @Transactional
    public void deleteCardhis(String cardhisid, LoginUser currentUser) {
        cardAnswerHisMapper.deleteByCardId(cardhisid);

        cardPointHisMapper.deleteByCardId(cardhisid);

        cardHisMapper.deleteEntity(cardhisid);
    }

}
