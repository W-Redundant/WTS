package com.wts.exam.service.impl;

import com.wts.exam.domain.Card;
import com.wts.exam.domain.Paper;
import com.wts.exam.domain.PaperUserown;
import com.wts.exam.domain.Room;
import com.farm.core.time.TimeTool;

import com.wts.exam.mapper.PaperUserownMapper;
import org.apache.log4j.Logger;
import com.wts.exam.dao.PaperUserOwnDaoInter;
import com.wts.exam.service.CardServiceInter;
import com.wts.exam.service.PaperServiceInter;
import com.wts.exam.service.PaperUserOwnServiceInter;
import com.wts.exam.service.RoomServiceInter;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.Resource;
import com.farm.core.auth.domain.LoginUser;

/* *
 *功能：用户答卷服务层实现类
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@Service
public class PaperUserOwnServiceImpl implements PaperUserOwnServiceInter {
	@Resource
	private PaperUserOwnDaoInter paperuserownDaoImpl;

	@Autowired
	private PaperUserownMapper paperUserownMapper;

	@Autowired
	private PaperServiceInter paperServiceImpl;
	@Autowired
	private CardServiceInter cardServiceImpl;
	@Autowired
	private RoomServiceInter roomServiceImpl;
	private static final Logger log = Logger
			.getLogger(PaperUserOwnServiceImpl.class);

	/**
	 * 删除超过数量的历史记录
	 * 
	 * @param userOwn
	 * @param user
	 */
	private PaperUserown insertAndUpdatePaperUserOwn(String modeltype,
                                                     String paperid, String papername, LoginUser user) {
		if (paperUserownMapper.countByCuserAndModelType(user.getId(),modeltype) > 1000) {
			// 如果大于20条记录，就删除5条最早的
			paperuserownDaoImpl.delEarliestSubject(user.getId(), modeltype);
		}
		paperUserownMapper.deleteByPaperIdAndModerTypeAndCuser(paperid,modeltype,user.getId());

		PaperUserown userOwn = new PaperUserown();
		userOwn.setCtime(TimeTool.getTimeDate14());
		userOwn.setCuser(user.getId());
		userOwn.setCusername(user.getName());
		userOwn.setModeltype(modeltype);
		userOwn.setPaperid(paperid);
		userOwn.setPapername(papername);
		userOwn.setRpcent(-1000);
		userOwn.setScore(-1000);
		userOwn.setPstate("1");
		paperUserownMapper.insertEntity(userOwn);
		return userOwn;
	}

	@Override
	@Transactional
	public PaperUserown editPaperuserownEntity(PaperUserown entity,
                                               LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		PaperUserown entity2 = paperUserownMapper.getEntity(entity.getId());
		// entity2.setEuser(user.getId());
		// entity2.setEusername(user.getName());
		// entity2.setEtime(TimeTool.getTimeDate14());
		entity2.setRoomname(entity.getRoomname());
		entity2.setPapername(entity.getPapername());
		entity2.setRoomid(entity.getRoomid());
		entity2.setPaperid(entity.getPaperid());
		entity2.setModeltype(entity.getModeltype());
		entity2.setPcontent(entity.getPcontent());
		entity2.setPstate(entity.getPstate());
		entity2.setCusername(entity.getCusername());
		entity2.setCuser(entity.getCuser());
		entity2.setCtime(entity.getCtime());
		entity2.setId(entity.getId());
		paperUserownMapper.editEntity(entity2);
		return entity2;
	}

	@Override
	@Transactional
	public void deletePaperuserownEntity(String id, LoginUser user) {
		PaperUserown paperUserOwn = paperUserownMapper.getEntity(id);
		String paperid = null;
		if (paperUserOwn.getModeltype().equals("2")) {
			paperid = paperUserOwn.getPaperid();
		}
		paperUserownMapper.deleteEntity(paperUserOwn.getId());
		if (paperid != null) {
			paperuserownDaoImpl.getSession().flush();
			paperServiceImpl
					.updateBooknum(
							paperid,
							paperUserownMapper.countByModelTypeAndPaperId("2",paperid));
		}
	}

	@Override
	@Transactional
	public PaperUserown getPaperuserownEntity(String id) {
		// TODO 自动生成代码,修改后请去除本注释
		if (id == null) {
			return null;
		}
		return paperUserownMapper.getEntity(id);
	}

	@Override
	@Transactional
	public DataQuery createPaperuserownSimpleQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery
				.init(query,
						"WTS_PAPER_USEROWN a left join WTS_PAPER b on a.paperid=b.id left join WTS_CARD c on a.cardid=c.id",
						"a.ID as ID,a.cardid as CARDID,a.ROOMNAME as ROOMNAME,a.PAPERNAME as PAPERNAME,c.PSTATE as CARDSTATE,a.ROOMID as ROOMID,a.PAPERID as PAPERID,a.MODELTYPE as MODELTYPE,a.PCONTENT as PCONTENT,a.PSTATE as PSTATE,a.CUSERNAME as CUSERNAME,a.CUSER as CUSER,a.CTIME as CTIME,a.SCORE as SCORE,a.RPCENT as RPCENT");
		return dbQuery;
	}

	@Override
	@Transactional
	public void addDoPaperInfo(String paperId, int RPcent, LoginUser user) {
		Paper paper = paperServiceImpl.getPaperEntity(paperId);
		PaperUserown userOwn = insertAndUpdatePaperUserOwn("1", paperId,
				paper.getName(), user);
		userOwn.setRpcent(RPcent);
		paperUserownMapper.editEntity(userOwn);
	}

	@Override
	@Transactional
	public void addDoPaperInfo(String cardId, LoginUser user) {
		Card card = cardServiceImpl.getCardEntity(cardId);
		Room room = roomServiceImpl.getRoomEntity(card.getRoomid());
		Paper paper = paperServiceImpl.getPaperEntity(card.getPaperid());
		PaperUserown userOwn = insertAndUpdatePaperUserOwn("1", paper.getId(),
				paper.getName(), user);
		userOwn.setRoomid(room.getId());
		userOwn.setRoomname(room.getName());
		userOwn.setCardid(cardId);
		paperUserownMapper.editEntity(userOwn);
	}

	@Override
	@Transactional
	public void refreshScore(String cardId, Float allnum) {
		List<PaperUserown> list = paperUserownMapper.findByCardId(cardId);

		if (list.size() > 0) {
			PaperUserown userown = list.get(0);
			PaperUserown entity = paperUserownMapper.getEntity(userown.getId());
			entity.setScore(allnum);
			paperUserownMapper.editEntity(entity);
		}
	}

	@Override
	@Transactional
	public boolean doBook(String roomid, String paperid, LoginUser user) {
		boolean isBook = paperUserownMapper.countByModelTypeAndPaperIdAndCuser("2",paperid,user.getId()) > 0;

		if (isBook) {
			// 取消
			paperUserownMapper.deleteByPaperIdAndModerTypeAndCuser(paperid,"2",user.getId());
		} else {
			Room room = roomServiceImpl.getRoomEntity(roomid);
			Paper paper = paperServiceImpl.getPaperEntity(paperid);
			// 訂閲
			PaperUserown userOwn = insertAndUpdatePaperUserOwn("2",
					paper.getId(), paper.getName(), user);
			userOwn.setRoomid(room.getId());
			userOwn.setRoomname(room.getName());
			paperUserownMapper.editEntity(userOwn);
		}

		paperServiceImpl.updateBooknum(
				paperid,
				paperUserownMapper.countByModelTypeAndPaperId("2",paperid));
		return !isBook;
	}

	@Override
	@Transactional
	public boolean isBook(String paperid, String userid) {
		return paperUserownMapper.countByModelTypeAndPaperIdAndCuser("2",paperid,userid)>0;
	}

}
