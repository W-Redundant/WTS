package com.farm.exam.service.impl;

import com.farm.exam.mapper.SubjectAnswerMapper;
import com.farm.exam.domain.SubjectAnswer;
import com.farm.core.time.TimeTool;
import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.FarmFileManagerInter.FILE_APPLICATION_TYPE;

import org.apache.commons.lang.StringUtils;
import com.farm.exam.dao.SubjectAnswerDaoInter;
import com.farm.exam.service.SubjectAnswerServiceInter;
import com.farm.core.sql.query.DBRule;
import com.farm.core.sql.query.DBRuleList;
import com.farm.core.sql.query.DataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.farm.core.auth.domain.LoginUser;

/* *
 *功能：考题答案服务层实现类
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@Service
public class SubjectAnswerServiceImpl implements SubjectAnswerServiceInter {
	@Autowired
	private SubjectAnswerDaoInter subjectanswerDaoImpl;

	@Autowired
	private SubjectAnswerMapper subjectAnswerMapper;

	@Autowired
	private FarmFileManagerInter farmFileManagerImpl;

	@Override
	@Transactional
	public SubjectAnswer insertSubjectanswerEntity(SubjectAnswer entity,
			LoginUser user) {
		entity.setCuser(user.getId());
		entity.setCtime(TimeTool.getTimeDate14());
		entity.setCusername(user.getName());
		entity.setPstate("1");
		if (entity.getAnswer() == null) {
			entity.setAnswer("");
		}
		if (StringUtils.isBlank(entity.getRightanswer())) {
			entity.setRightanswer("2");
		}
		if (entity.getSort() == null) {
			entity.setSort(99);
		}
		subjectAnswerMapper.insertEntity(entity);
		// -------------------------------------
		farmFileManagerImpl
				.submitFileByAppHtml(entity.getAnswernote(),
						entity.getVersionid(),
						FILE_APPLICATION_TYPE.SUBJECT_ANSWERNOTE);
		return entity;
	}

	@Override
	@Transactional
	public SubjectAnswer editSubjectanswerEntity(SubjectAnswer entity,
			LoginUser user) {
		SubjectAnswer entity2 = subjectAnswerMapper.getEntity(entity.getId());
		String oldText = entity2.getAnswernote();
		entity2.setRightanswer(entity.getRightanswer());
		entity2.setAnswernote(entity.getAnswernote());
		entity2.setSort(entity.getSort());
		if (entity.getAnswer() == null) {
			entity2.setAnswer("");
		} else {
			entity2.setAnswer(entity.getAnswer());
		}
		entity2.setPointweight(entity.getPointweight());
		entity2.setPcontent(entity.getPcontent());
		entity2.setId(entity.getId());
		if (StringUtils.isBlank(entity2.getRightanswer())) {
			entity2.setRightanswer("2");
		}
		if (entity2.getSort() == null) {
			entity2.setSort(99);
		}
		subjectAnswerMapper.editEntity(entity2);
		// ------------------
		farmFileManagerImpl.updateFileByAppHtml(oldText,
				entity2.getAnswernote(), entity.getVersionid(),
				FILE_APPLICATION_TYPE.SUBJECT_ANSWERNOTE);
		return entity2;
	}

	@Override
	@Transactional
	public void deleteSubjectanswerEntity(String id, LoginUser user) {
		subjectAnswerMapper.deleteEntity(id);
		// 此处不取消该答案的附件状态，将在删除题的时候进行取消
	}

	@Override
	@Transactional
	public SubjectAnswer getSubjectanswerEntity(String id) {
		// TODO 自动生成代码,修改后请去除本注释
		if (id == null) {
			return null;
		}
		return subjectAnswerMapper.getEntity(id);
	}

	@Override
	@Transactional
	public DataQuery createSubjectanswerSimpleQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery
				.init(query,
						"WTS_SUBJECT_ANSWER",
						"ID,RIGHTANSWER,ANSWERNOTE,SORT,ANSWER,VERSIONID,PCONTENT,PSTATE,CUSER,CUSERNAME,CTIME,POINTWEIGHT");
		return dbQuery;
	}

	@Override
	@Transactional
	public void setOnlyRight(String answerId, LoginUser currentUser) {
		SubjectAnswer answerRight = subjectAnswerMapper.getEntity(answerId);
		List<SubjectAnswer> answers = subjectanswerDaoImpl
				.selectEntitys(DBRuleList
						.getInstance()
						.add(new DBRule("VERSIONID",
								answerRight.getVersionid(), "=")).toList());
		for (SubjectAnswer ans : answers) {
			ans.setRightanswer("0");
			ans.setCtime(TimeTool.getTimeDate14());
			ans.setCuser(currentUser.getId());
			ans.setCusername(currentUser.getName());
			subjectAnswerMapper.editEntity(ans);
		}
		answerRight.setRightanswer("1");
		answerRight.setCtime(TimeTool.getTimeDate14());
		answerRight.setCuser(currentUser.getId());
		answerRight.setCusername(currentUser.getName());
		subjectAnswerMapper.editEntity(answerRight);
	}

	@Override
	@Transactional
	public void setRightAnswer(String answerId, boolean isTrue,
			LoginUser currentUser) {
		SubjectAnswer answerRight = subjectanswerDaoImpl.getEntity(answerId);
		answerRight.setRightanswer(isTrue ? "1" : "0");
		answerRight.setCtime(TimeTool.getTimeDate14());
		answerRight.setCuser(currentUser.getId());
		answerRight.setCusername(currentUser.getName());
		subjectanswerDaoImpl.editEntity(answerRight);
	}

}
