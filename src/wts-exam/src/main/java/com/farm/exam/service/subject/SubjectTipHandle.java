package com.farm.exam.service.subject;

import java.util.List;

import com.farm.core.auth.domain.LoginUser;
import com.farm.exam.dao.SubjectDaoInter;
import com.farm.exam.dao.SubjectAnswerDaoInter;
import com.farm.exam.dao.SubjectVersionDaoInter;
import com.farm.exam.domain.CardAnswer;
import com.farm.exam.domain.Subject;
import com.farm.exam.domain.SubjectAnswer;
import com.farm.exam.domain.SubjectVersion;
import com.farm.exam.domain.ex.SubjectUnit;
import com.farm.exam.service.SubjectServiceInter;

/**
 * 类型题目的特殊处理逻辑
 * 
 * @author macpl
 *
 */
public interface SubjectTipHandle {

	/**
	 * 题目初始化的时候执行
	 * 
	 * @param subject
	 * @param version
	 * @param subjectanswerDaoImpl
	 * @param subjectDaoImpl
	 * @param subjectversionDaoImpl
	 * @param user
	 */
	public void subjectInitHandle(Subject subject, SubjectVersion version,
			SubjectAnswerDaoInter subjectanswerDaoImpl,
			SubjectDaoInter subjectDaoImpl,
			SubjectVersionDaoInter subjectversionDaoImpl, LoginUser user);

	/**
	 * 用户批量导入题后，某个类型的字符串题创建为数据库中的数据
	 * 
	 * @return
	 */
	public SubjectUnit expressTextSubject(String subjectText,
			String subjectTypeId, LoginUser currentUser,
			SubjectServiceInter subjectServer,
			SubjectAnswerDaoInter subjectanswerDaoImpl,
			SubjectDaoInter subjectDaoImpl,
			SubjectVersionDaoInter subjectversionDaoImpl);

	/**
	 * 判断一道题是否有答案
	 * 
	 * @param answers
	 * @return
	 */
	public boolean isHaveAnswer(List<CardAnswer> answers);

	/**
	 * 把一个答案填充到题中
	 * 
	 * @param nuit
	 *            题
	 * @param list
	 *            用户答案序列
	 */
	public void loadVal(SubjectUnit nuit, List<CardAnswer> list);

	/**
	 * 计算用户答题得得分权重
	 * 
	 * @param unit
	 * @return 返回得分权重，如果得分大于100，或者小于0，则都是最终的分数
	 */
	public int runPointWeight(SubjectUnit unit);

	/**
	 * 判斷該題是否已經配置過正确答案了
	 * 
	 * @param answers
	 *            题的答案
	 * @return
	 */
	public boolean isHaveRightAnswer(List<SubjectAnswer> answers);

}
