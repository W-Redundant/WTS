package com.farm.exam.dao.impl;

import java.math.BigInteger;

import com.farm.exam.domain.PaperUserown;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.farm.exam.dao.PaperUserOwnDaoInter;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.sql.result.DataResult;
import com.farm.core.sql.utils.HibernateSQLTools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/* *
 *功能：用户答卷持久层实现
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@Repository
public class PaperUserOwnDaoImpl extends HibernateSQLTools<PaperUserown>
		implements PaperUserOwnDaoInter {
	@Autowired
	private SessionFactory sessionFatory;
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public void deleteEntity(PaperUserown paperuserown) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.delete(paperuserown);
	}

	@Override
	public int getAllListNum() {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		SQLQuery sqlquery = session
				.createSQLQuery("select count(*) from farm_code_field");
		BigInteger num = (BigInteger) sqlquery.list().get(0);
		return num.intValue();
	}

	@Override
	public PaperUserown getEntity(String paperuserownid) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		return (PaperUserown) session.get(PaperUserown.class, paperuserownid);
	}

	@Override
	public PaperUserown insertEntity(PaperUserown paperuserown) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.save(paperuserown);
		return paperuserown;
	}

	@Override
	public void editEntity(PaperUserown paperuserown) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.update(paperuserown);
	}

	@Override
	public Session getSession() {
		// TODO 自动生成代码,修改后请去除本注释
		return sessionFatory.getCurrentSession();
	}

	@Override
	public DataResult runSqlQuery(DataQuery query) {
		// TODO 自动生成代码,修改后请去除本注释
		try {
			return query.search(sessionFatory.getCurrentSession());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void deleteEntitys(List<DBRule> rules) {
		// TODO 自动生成代码,修改后请去除本注释
		deleteSqlFromFunction(sessionFatory.getCurrentSession(), rules);
	}

	@Override
	public List<PaperUserown> selectEntitys(List<DBRule> rules) {
		// TODO 自动生成代码,修改后请去除本注释
		return selectSqlFromFunction(sessionFatory.getCurrentSession(), rules);
	}

	@Override
	public void updataEntitys(Map<String, Object> values, List<DBRule> rules) {
		// TODO 自动生成代码,修改后请去除本注释
		updataSqlFromFunction(sessionFatory.getCurrentSession(), values, rules);
	}

	@Override
	public int countEntitys(List<DBRule> rules) {
		// TODO 自动生成代码,修改后请去除本注释
		return countSqlFromFunction(sessionFatory.getCurrentSession(), rules);
	}

	public SessionFactory getSessionFatory() {
		return sessionFatory;
	}

	public void setSessionFatory(SessionFactory sessionFatory) {
		this.sessionFatory = sessionFatory;
	}

	@Override
	protected Class<?> getTypeClass() {
		return PaperUserown.class;
	}

	@Override
	protected SessionFactory getSessionFactory() {
		return sessionFatory;
	}

	@Override
	public void delEarliestSubject(String userid, String modeltype) {
		Session session = sessionFatory.getCurrentSession();
		//
		SQLQuery idsQuery = session
				.createSQLQuery("SELECT ID FROM  WTS_PAPER_USEROWN WHERE CUSER = ? AND MODELTYPE = ? ORDER BY CTIME ASC LIMIT 0,5");
		idsQuery.setString(0, userid);
		idsQuery.setString(1, modeltype);
		List<String> ids = idsQuery.list();
		if (ids.size() <= 0) {
			return;
		}
		String idsSql = null;
		for (String id : ids) {
			if (idsSql == null) {
				idsSql = "'" + id + "'";
			} else {
				idsSql = idsSql + ",'" + id + "'";
			}
		}
		SQLQuery sqlquery = session
				.createSQLQuery("DELETE FROM WTS_PAPER_USEROWN WHERE id IN ("
						+ idsSql + ") and CUSER = ? AND MODELTYPE = ?");
		sqlquery.setString(0, userid);
		sqlquery.setString(1, modeltype);
		sqlquery.executeUpdate();

	}
}
