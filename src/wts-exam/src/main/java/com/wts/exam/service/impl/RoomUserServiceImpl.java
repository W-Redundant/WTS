package com.wts.exam.service.impl;

import com.wts.exam.domain.RoomUser;
import com.farm.core.time.TimeTool;
import com.wts.exam.mapper.RoomUserMapper;
import org.apache.log4j.Logger;
import com.wts.exam.dao.RoomUserDaoInter;
import com.wts.exam.service.RoomuUserServiceInter;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import com.farm.core.auth.domain.LoginUser;

@Service
public class RoomUserServiceImpl implements RoomuUserServiceInter {

	@Autowired
	private RoomUserMapper roomUserMapper;

	@Override
	@Transactional
	public RoomUser insertRoomuserEntity(RoomUser entity, LoginUser user) {
		roomUserMapper.insertEntity(entity);
		return entity;
	}

	@Override
	@Transactional
	public RoomUser editRoomuserEntity(RoomUser entity, LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		RoomUser entity2 = roomUserMapper.getEntity(entity.getId());
		entity2.setId(entity.getId());
		entity2.setUserid(entity.getUserid());
		entity2.setRoomid(entity.getRoomid());
		roomUserMapper.editEntity(entity2);
		return entity2;
	}

	@Override
	@Transactional
	public void deleteRoomuserEntity(String id, LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		roomUserMapper.deleteEntity(id);
	}

	@Override
	@Transactional
	public RoomUser getRoomuserEntity(String id) {
		// TODO 自动生成代码,修改后请去除本注释
		if (id == null) {
			return null;
		}
		return roomUserMapper.getEntity(id);
	}

	@Override
	@Transactional
	public DataQuery createRoomuserSimpleQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery
				.init(query,
						"WTS_ROOM_USER A LEFT JOIN ALONE_AUTH_USER B ON A.USERID=B.ID LEFT JOIN WTS_ROOM C ON A.ROOMID=C.ID LEFT JOIN ALONE_AUTH_USERORG d on d.USERID=a.USERID LEFT JOIN ALONE_AUTH_ORGANIZATION e on e.ID=d.ORGANIZATIONID left join WTS_CARD f on f.USERID=a.USERID and f.ROOMID=a.ROOMID left join wts_paper g on g.ID=f.PAPERID",
						"C.NAME AS ROOMNAME,B.NAME AS USERNAME,A.ID as ID,E.NAME as ORGNAME,f.STARTTIME as STARTTIME,g.NAME as PAPERNAME,F.OVERTIME as OVERTIME");
		return dbQuery;
	}

	@Override
	@Transactional
	public void insertRoomUser(String id, String userid, LoginUser currentUser) {
		// 先删除
		roomUserMapper.deleteByRoomIDAndUserId(id,userid);

		// 在添加
		RoomUser entity = new RoomUser();
		entity.setUserid(userid);
		entity.setRoomid(id);
		roomUserMapper.insertEntity(entity);
	}

}
