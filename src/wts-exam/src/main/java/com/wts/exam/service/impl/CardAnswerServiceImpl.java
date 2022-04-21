package com.wts.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DataQuery;
import com.wts.exam.domain.CardAnswer;
import com.wts.exam.mapper.CardAnswerMapper;
import com.wts.exam.service.CardAnswerServiceInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CardAnswerServiceImpl implements CardAnswerServiceInter {
    @Autowired
    private CardAnswerMapper cardAnswerMapper;

    @Override
    @Transactional
    public CardAnswer insertCardAnswerEntity(CardAnswer entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        // entity.setCuser(user.getId());
        // entity.setCtime(TimeTool.getTimeDate14());
        // entity.setCusername(user.getName());
        // entity.setEuser(user.getId());
        // entity.setEusername(user.getName());
        // entity.setEtime(TimeTool.getTimeDate14());
        // entity.setPstate("1");
        cardAnswerMapper.insertEntity(entity);
        return entity;
    }

    @Override
    @Transactional
    public CardAnswer editCardAnswerEntity(CardAnswer entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        CardAnswer entity2 = cardAnswerMapper.getEntity(entity.getId());
        // entity2.setEuser(user.getId());
        // entity2.setEusername(user.getName());
        // entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setPstate(entity.getPstate());
        entity2.setPcontent(entity.getPcontent());
        entity2.setCtime(entity.getCtime());
        entity2.setValstr(entity.getValstr());
        entity2.setCuser(entity.getCuser());
        entity2.setVersionid(entity.getVersionid());
        entity2.setAnswerid(entity.getAnswerid());
        entity2.setCardid(entity.getCardid());
        entity2.setId(entity.getId());
        cardAnswerMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteCardAnswerEntity(String id, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        cardAnswerMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public CardAnswer getCardAnswerEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return cardAnswerMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createCardAnswerSimpleQuery(DataQuery query) {
        // TODO 自动生成代码,修改后请去除本注释
        DataQuery dbQuery = DataQuery
                .init(query, "WTS_CARD_ANSWER",
                        "ID,PSTATE,PCONTENT,CTIME,VALSTR,CUSER,VERSIONID,ANSWERID,CARDID");
        return dbQuery;
    }

}
