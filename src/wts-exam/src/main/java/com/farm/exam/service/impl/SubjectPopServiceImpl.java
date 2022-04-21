package com.farm.exam.service.impl;

import com.farm.exam.mapper.SubjectPopMapper;
import com.farm.exam.domain.SubjectPop;
import com.farm.exam.domain.SubjectType;
import com.farm.exam.dao.SubjectPopDaoInter;
import com.farm.exam.service.SubjectPopServiceInter;
import com.farm.exam.service.SubjectTypeServiceInter;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.farm.authority.FarmAuthorityService;
import com.farm.core.auth.domain.LoginUser;

@Service
public class SubjectPopServiceImpl implements SubjectPopServiceInter {
	@Autowired
	private SubjectPopDaoInter subjectpopDaoImpl;

	@Autowired
	private SubjectPopMapper subjectPopMapper;

	@Autowired
	private SubjectTypeServiceInter subjectTypeServiceImpl;

	@Override
	@Transactional
	public SubjectPop insertSubjectpopEntity(SubjectPop entity, LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		// entity.setCuser(user.getId());
		// entity.setCtime(TimeTool.getTimeDate14());
		// entity.setCusername(user.getName());
		// entity.setEuser(user.getId());
		// entity.setEusername(user.getName());
		// entity.setEtime(TimeTool.getTimeDate14());
		// entity.setPstate("1");
		subjectPopMapper.insertEntity(entity);
		return entity;
	}

	@Override
	@Transactional
	public SubjectPop editSubjectpopEntity(SubjectPop entity, LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		SubjectPop entity2 = subjectPopMapper.getEntity(entity.getId());
		// entity2.setEuser(user.getId());
		// entity2.setEusername(user.getName());
		// entity2.setEtime(TimeTool.getTimeDate14());
		entity2.setTypeid(entity.getTypeid());
		entity2.setUsername(entity.getUsername());
		entity2.setUserid(entity.getUserid());
		entity2.setFuntype(entity.getFuntype());
		entity2.setId(entity.getId());
		subjectPopMapper.editEntity(entity2);
		return entity2;
	}

	@Override
	@Transactional
	public SubjectPop getSubjectpopEntity(String id) {
		// TODO 自动生成代码,修改后请去除本注释
		if (id == null) {
			return null;
		}
		return subjectPopMapper.getEntity(id);
	}

	@Override
	@Transactional
	public DataQuery createSubjectpopSimpleQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery
				.init(query,
						"WTS_SUBJECT_POP a left join WTS_SUBJECT_TYPE b on a.TYPEID=b.ID",
						"a.FUNTYPE as FUNTYPE,a.USERNAME as USERNAME,b.NAME as TYPENAME ,a.ID as ID");
		return dbQuery;
	}

	@Override
	@Transactional
	public void deleteSubjectpopEntity(String id, LoginUser user) {
		SubjectPop pop = subjectPopMapper.getEntity(id);
		{
			List<String> ids = new ArrayList<>();
			ids.add(pop.getTypeid());
			List<String> typeIds = subjectTypeServiceImpl.getAllSubType(ids);
			for (String typeid : typeIds) {
				subjectpopDaoImpl.deleteEntitys(DBRuleList.getInstance()
						.add(new DBRule("TYPEID", typeid, "="))
						.add(new DBRule("USERID", pop.getUserid(), "="))
						.add(new DBRule("FUNTYPE", pop.getFuntype(), "="))
						.toList());

				List<SubjectPop> pops = subjectpopDaoImpl
						.selectEntitys(DBRuleList
								.getInstance()
								.add(new DBRule("TYPEID", typeid, "="))
								.add(new DBRule("FUNTYPE", pop.getFuntype(),
										"=")).toList());
				if (pops.size() == 0) {
					SubjectType type = subjectTypeServiceImpl
							.getSubjecttypeEntity(typeid);
					if (pop.getFuntype().equals("1")) {
						// 使用权，设置为指定人员
						type.setReadpop("1");
					}
					if (pop.getFuntype().equals("2")) {
						// 编辑权，设置为指定人员
						type.setWritepop("1");
					}
					subjectTypeServiceImpl.editSubjecttypeEntity(type, user);
				}
			}
		}
	}

	@Override
	@Transactional
	public void insertPop(List<String> userIds, List<String> typeIds,
			String functype, LoginUser currentUser) {
		typeIds = subjectTypeServiceImpl.getAllSubType(typeIds);
		for (String typeid : typeIds) {
			for (String userid : userIds) {
				SubjectPop pop = new SubjectPop();
				pop.setFuntype(functype);
				pop.setTypeid(typeid);
				pop.setUserid(userid);
				LoginUser user = FarmAuthorityService.getInstance()
						.getUserById(userid);
				if (user != null) {
					pop.setUsername(user.getName());
					subjectpopDaoImpl
							.deleteEntitys(DBRuleList.getInstance()
									.add(new DBRule("TYPEID", typeid, "="))
									.add(new DBRule("USERID", userid, "="))
									.add(new DBRule("FUNTYPE", functype, "="))
									.toList());
					subjectPopMapper.insertEntity(pop);
					SubjectType type = subjectTypeServiceImpl
							.getSubjecttypeEntity(typeid);
					if (functype.equals("1")) {
						// 使用权，设置为指定人员
						type.setReadpop("2");
					}
					if (functype.equals("2")) {
						// 编辑权，设置为指定人员
						type.setWritepop("2");
					}
					subjectTypeServiceImpl.editSubjecttypeEntity(type,
							currentUser);
				}
			}
		}
	}
}
