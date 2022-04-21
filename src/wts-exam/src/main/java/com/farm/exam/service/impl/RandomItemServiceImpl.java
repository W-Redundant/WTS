package com.farm.exam.service.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.sql.query.DBSort;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import com.farm.exam.mapper.RandomItemMapper;
import com.farm.exam.mapper.RandomStepMapper;
import com.farm.exam.domain.RandomItem;
import com.farm.exam.domain.RandomStep;
import com.farm.exam.service.RandomItemServiceInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RandomItemServiceImpl implements RandomItemServiceInter {

    @Autowired
    private RandomItemMapper randomItemMapper;
    @Autowired
    private RandomStepMapper randomStepMapper;

    @Override
    @Transactional
    public RandomItem insertRandomitemEntity(RandomItem entity, LoginUser user) {
        entity.setCuser(user.getId());
        entity.setCtime(TimeTool.getTimeDate14());
        randomItemMapper.insertEntity(entity);
        return entity;
    }

    @Override
    @Transactional
    public RandomItem editRandomitemEntity(RandomItem entity, LoginUser user) {
        RandomItem entity2 = randomItemMapper.getEntity(entity.getId());
        entity2.setName(entity.getName());
        entity2.setPcontent(entity.getPcontent());
        entity2.setPstate(entity.getPstate());
        randomItemMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteRandomitemEntity(String id, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        randomItemMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public RandomItem getRandomitemEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return randomItemMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createRandomitemSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery
                .init(query,
                        "(select a.ID as ID,a.NAME as NAME,a.PCONTENT as PCONTENT,a.PSTATE as PSTATE,a.CUSER as CUSER,a.CTIME as CTIME,count(b.id) as NUM,sum(b.subnum) as SUBNUM from WTS_RANDOM_ITEM a left join WTS_RANDOM_STEP b on b.itemid=a.id group by a.id) t ",
                        "ID,NAME,PCONTENT,PSTATE,CUSER,CTIME,NUM,SUBNUM");
        dbQuery.setDefaultSort(new DBSort("CTIME", "ASC"));
        return dbQuery;
    }

    @Override
    @Transactional
    public List<RandomItem> getLiveRandomItems() {
        return randomItemMapper.findByPstate("1");
    }

    @Override
    @Transactional
    public List<RandomStep> getSteps(String itemid) {
        return randomStepMapper.findByItemId(itemid);
    }

}
