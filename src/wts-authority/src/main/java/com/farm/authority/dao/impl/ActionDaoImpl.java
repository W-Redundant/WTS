package com.farm.authority.dao.impl;

import com.farm.authority.dao.ActionDaoInter;
import com.farm.authority.domain.Action;
import com.farm.authority.mapper.ActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActionDaoImpl implements ActionDaoInter {

    @Autowired
    private ActionMapper actionMapper;

    @Override
    public List<Action> getAllEntity() {
        return actionMapper.selectAll();
    }

}
