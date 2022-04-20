package com.farm.doc.server.impl;

import com.farm.core.auth.domain.LoginUser;
import com.farm.core.inter.domain.MessageType;
import com.farm.core.message.MessageTypeFactory;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.time.TimeTool;
import com.farm.doc.domain.MessageModel;
import com.farm.doc.domain.MessageSender;
import com.farm.doc.mapper.MessageModelMapper;
import com.farm.doc.mapper.MessageSenderMapper;
import com.farm.doc.server.MessagemodelServiceInter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class MessagemodelServiceImpl implements MessagemodelServiceInter {

    @Autowired
    private MessageModelMapper messageModelMapper;

    @Autowired
    private MessageSenderMapper messageSenderMapper;

    @Override
    @Transactional
    public MessageModel insertMessagemodelEntity(MessageModel entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        // entity.setCuser(user.getId());
        // entity.setCtime(TimeTool.getTimeDate14());
        // entity.setCusername(user.getName());
        // entity.setEuser(user.getId());
        // entity.setEusername(user.getName());
        // entity.setEtime(TimeTool.getTimeDate14());
        // entity.setPstate("1");
        messageModelMapper.insertEntity(entity);
        return entity;
    }

    @Override
    @Transactional
    public MessageModel editMessagemodelEntity(MessageModel entity, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        MessageModel entity2 = messageModelMapper.getEntity(entity.getId());
        // entity2.setEuser(user.getId());
        // entity2.setEusername(user.getName());
        // entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setContentmodel(entity.getContentmodel());
        entity2.setTypekey(entity.getTypekey());
        entity2.setOverer(entity.getOverer());
        entity2.setTitlemodel(entity.getTitlemodel());
        entity2.setPcontent(entity.getPcontent());
        entity2.setTitle(entity.getTitle());
        entity2.setPstate(entity.getPstate());
        entity2.setEuser(entity.getEuser());
        entity2.setEusername(entity.getEusername());
        entity2.setCuser(entity.getCuser());
        entity2.setCusername(entity.getCusername());
        entity2.setCtime(entity.getCtime());
        entity2.setEtime(entity.getEtime());
        entity2.setId(entity.getId());
        messageModelMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    @Transactional
    public void deleteMessagemodelEntity(String id, LoginUser user) {
        // TODO 自动生成代码,修改后请去除本注释
        messageModelMapper.deleteEntity(id);
    }

    @Override
    @Transactional
    public MessageModel getMessagemodelEntity(String id) {
        // TODO 自动生成代码,修改后请去除本注释
        if (id == null) {
            return null;
        }
        return messageModelMapper.getEntity(id);
    }

    @Override
    @Transactional
    public DataQuery createMessagemodelSimpleQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery.init(query,
                "( SELECT ID, CONTENTMODEL, TYPEKEY, OVERER, TITLEMODEL, PCONTENT, TITLE, PSTATE, EUSER, EUSERNAME, CUSER, CUSERNAME, CTIME, ETIME, ( SELECT count(id) FROM FARM_MESSAGE_SENDER WHERE modelid = FARM_MESSAGE_MODEL.id ) num FROM FARM_MESSAGE_MODEL ) a",
                "ID,CONTENTMODEL,TYPEKEY,OVERER,TITLEMODEL,PCONTENT,TITLE,PSTATE,EUSER,EUSERNAME,CUSER,CUSERNAME,CTIME,ETIME,NUM");
        return dbQuery;
    }

    @Override
    @Transactional
    public void initModel(LoginUser currentUser) {
        // 先删除表中全部数据
        messageSenderMapper.deleteAll();
        messageModelMapper.deleteAll();
        // 从系统中加载默认数据
        for (String key : MessageTypeFactory.getInstance().getKeys()) {
            MessageType type = MessageTypeFactory.getInstance().getBaseType(key);
            MessageModel model = new MessageModel();
            model.setContentmodel(type.getContentModel());
            model.setCtime(TimeTool.getTimeDate14());
            model.setCuser(currentUser.getId());
            model.setCusername(currentUser.getName());
            model.setEtime(TimeTool.getTimeDate14());
            model.setEuser(currentUser.getId());
            model.setEusername(currentUser.getName());
            model.setOverer(type.getSenderDescribe());
            // model.setPcontent(pcontent);
            model.setPstate("1");
            model.setTitle(type.getTypeName());
            model.setTitlemodel(type.getTitleModel());
            model.setTypekey(type.getKey().name());
            messageModelMapper.insertEntity(model);
        }

    }

    @Override
    @Transactional
    public void editState(String modelid, boolean isAble, LoginUser currentUser) {
        MessageModel model = messageModelMapper.getEntity(modelid);
        model.setPstate(isAble ? "1" : "0");
        messageModelMapper.editEntity(model);
    }

    @Override
    @Transactional
    public MessageModel editMessagemodelEntity(String id, String titlemodel, String contentmodel, String pcontent,
                                               LoginUser currentUser) {
        MessageModel entity2 = messageModelMapper.getEntity(id);
        entity2.setEuser(currentUser.getId());
        entity2.setEusername(currentUser.getName());
        entity2.setEtime(TimeTool.getTimeDate14());
        entity2.setContentmodel(contentmodel);
        entity2.setTitlemodel(titlemodel);
        entity2.setPcontent(pcontent);
        messageModelMapper.editEntity(entity2);
        return entity2;
    }

    @Override
    public DataQuery createMessagemodelSendersQuery(DataQuery query) {
        DataQuery dbQuery = DataQuery.init(query,
                "FARM_MESSAGE_SENDER a left join ALONE_AUTH_USER b on a.APPID=b.id left join ALONE_AUTH_USERORG c on b.id=c.userid left join ALONE_AUTH_ORGANIZATION d on c.organizationid=d.id",
                "a.id as ID,b.NAME as NAME,d.NAME as ORGNAME");
        return dbQuery;
    }

    @Override
    @Transactional
    public void addModelSender(String modelid, String userid) {
        messageSenderMapper.deleteByAppidAndModelid(userid, modelid);
        MessageSender sender = new MessageSender();
        sender.setAppid(userid);
        sender.setModelid(modelid);
        sender.setType("1");
        messageSenderMapper.insertEntity(sender);
    }

    @Override
    @Transactional
    public void delModelSender(String id) {
        messageSenderMapper.deleteByPrimaryKey(id);
    }
}
