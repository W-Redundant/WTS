package com.farm.exam.dao.impl;

import java.math.BigInteger;

import com.farm.exam.domain.SubjectVersion;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.farm.exam.dao.SubjectVersionDaoInter;
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
 *功能：考题版本持久层实现
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@Repository
public class SubjectVersionDaoImpl extends HibernateSQLTools<SubjectVersion>
		implements SubjectVersionDaoInter {
	@Autowired
	private SessionFactory sessionFatory;
	private SqlSessionFactory sqlSessionFactory;

	@Override
	public void deleteEntity(SubjectVersion subjectversion) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.delete(subjectversion);
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
	public SubjectVersion getEntity(String subjectversionid) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		return (SubjectVersion) session.get(SubjectVersion.class,
				subjectversionid);
	}

	@Override
	public SubjectVersion insertEntity(SubjectVersion subjectversion) {
		// TODO 自动生成代码,修改后请去除本注释
		Session session = sessionFatory.getCurrentSession();
		session.save(subjectversion);
		return subjectversion;
	}

	@Override
	public void editEntity(SubjectVersion subjectversion) {
		Session session = sessionFatory.getCurrentSession();
		if (subjectversion.getTipstr().length() > 256) {
			subjectversion.setTipstr(subjectversion.getTipstr().substring(0,
					256));
		}
		session.update(subjectversion);
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
	public List<SubjectVersion> selectEntitys(List<DBRule> rules) {
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
		return SubjectVersion.class;
	}

	@Override
	protected SessionFactory getSessionFactory() {
		return sessionFatory;
	}

	@Override
	public List<SubjectVersion> getVersionsByPaperId(String paperId) {
		Session session = sessionFatory.getCurrentSession();
		SQLQuery sqlquery = session
				.createSQLQuery("select distinct b.ID as ID,b.CTIME as CTIME,b.CUSERNAME as CUSERNAME,b.CUSER as CUSER,b.PSTATE as PSTATE,b.PCONTENT as PCONTENT,b.TIPSTR as TIPSTR,b.TIPNOTE as TIPNOTE,b.TIPTYPE as TIPTYPE,b.SUBJECTID as SUBJECTID,b.ANSWERED as ANSWERED from WTS_PAPER_SUBJECT a left join WTS_SUBJECT_VERSION b on a.SUBJECTID=b.SUBJECTID where a.paperid=? and b.id is not null");
		sqlquery.setString(0, paperId);
		@SuppressWarnings("unchecked")
		List<SubjectVersion> list = (List<SubjectVersion>) sqlquery.addEntity(
				SubjectVersion.class).list();
		return list;
	}
}
