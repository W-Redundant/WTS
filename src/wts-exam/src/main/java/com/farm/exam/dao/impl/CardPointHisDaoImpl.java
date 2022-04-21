package com.farm.exam.dao.impl;

import java.math.BigInteger;

import com.farm.exam.domain.CardPointHis;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.farm.exam.dao.CardPointHisDaoInter;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.sql.result.DataResult;
import com.farm.core.sql.utils.HibernateSQLTools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/* *
 *功能：用户得分历史记录持久层实现
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@Repository
public class CardPointHisDaoImpl extends HibernateSQLTools<CardPointHis>
		implements CardPointHisDaoInter {
	@Autowired
	private SessionFactory sessionFatory;
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public void deleteEntity(CardPointHis cardpointhis) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.delete(cardpointhis);
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
	public CardPointHis getEntity(String cardpointhisid) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		return (CardPointHis) session.get(CardPointHis.class, cardpointhisid);
	}

	@Override
	public CardPointHis insertEntity(CardPointHis cardpointhis) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.save(cardpointhis);
		return cardpointhis;
	}

	@Override
	public void editEntity(CardPointHis cardpointhis) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.update(cardpointhis);
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
	public List<CardPointHis> selectEntitys(List<DBRule> rules) {
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
		return CardPointHis.class;
	}

	@Override
	protected SessionFactory getSessionFactory() {
		return sessionFatory;
	}

	@Override
	public void backup(String cardid) {
		if (countEntitys(DBRuleList.getInstance()
				.add(new DBRule("CARDID", cardid, "=")).toList()) <= 0) {
			// 如果答题卡备份过就不用再备份了
			Session session = sessionFatory.getCurrentSession();
			SQLQuery sql = session
					.createSQLQuery("insert into WTS_CARD_POINT_HIS (select * from WTS_CARD_POINT where cardid=?)");
			sql.setString(0, cardid);
			sql.executeUpdate();
		}
	}
}
