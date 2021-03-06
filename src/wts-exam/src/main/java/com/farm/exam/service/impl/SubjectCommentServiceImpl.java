package com.farm.exam.service.impl;

import com.farm.exam.mapper.SubjectCommentMapper;
import com.farm.exam.domain.SubjectComment;
import com.farm.core.time.TimeTool;
import com.farm.exam.dao.SubjectCommentDaoInter;
import com.farm.exam.service.SubjectCommentServiceInter;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.farm.core.auth.domain.LoginUser;

@Service
public class SubjectCommentServiceImpl implements SubjectCommentServiceInter {
	@Autowired
	private SubjectCommentDaoInter SubjectCommentDaoImpl;

	@Autowired
	private SubjectCommentMapper subjectCommentMapper;

	@Override
	@Transactional
	public void deleteSubjectCommentEntity(String id, LoginUser user) {
		subjectCommentMapper.deleteEntity(id);
	}

	@Override
	@Transactional
	public SubjectComment getSubjectCommentEntity(String id) {
		if (id == null) {
			return null;
		}
		return subjectCommentMapper.getEntity(id);
	}

	@Override
	@Transactional
	public DataQuery createSubjectCommentSimpleQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery.init(query, "WTS_SUBJECT_COMMENT",
				"ID,SUBJECTID,TEXT,PCONTENT,PSTATE,CUSERNAME,CTIME");
		return dbQuery;
	}

	@Override
	@Transactional
	public List<SubjectComment> getSubjectComments(String subjectid) {
		List<SubjectComment> list = SubjectCommentDaoImpl
				.selectEntitys(DBRuleList.getInstance()
						.add(new DBRule("SUBJECTID", subjectid, "=")).toList());
		Collections.sort(list, new Comparator<SubjectComment>() {
			@Override
			public int compare(SubjectComment o1, SubjectComment o2) {
				return o1.getCtime().compareTo(o2.getCtime());
			}
		});
		return list;
	}

	@Override
	@Transactional
	public SubjectComment insertSubjectComment(String text, String subjectid,
			LoginUser currentUser) {
		SubjectComment entity = new SubjectComment();
		entity.setCuser(currentUser.getId());
		entity.setCtime(TimeTool.getTimeDate14());
		entity.setCusername(currentUser.getName());
		entity.setText(text);
		entity.setSubjectid(subjectid);
		entity.setPstate("1");
		subjectCommentMapper.insertEntity(entity);
		return entity;
	}

}
