package com.farm.exam.service.impl;

import com.farm.exam.mapper.PaperSubjectMapper;
import com.farm.exam.domain.PaperSubject;
import lombok.extern.slf4j.Slf4j;
import com.farm.exam.service.PaperSubjectServiceInter;
import com.farm.core.sql.query.DataQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.farm.core.auth.domain.LoginUser;

@Service
@Slf4j
public class PaperSubjectServiceImpl implements PaperSubjectServiceInter {

	@Autowired
	private PaperSubjectMapper paperSubjectMapper;

	@Override
	@Transactional
	public PaperSubject insertPapersubjectEntity(PaperSubject entity,
			LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		// entity.setCuser(user.getId());
		// entity.setCtime(TimeTool.getTimeDate14());
		// entity.setCusername(user.getName());
		// entity.setEuser(user.getId());
		// entity.setEusername(user.getName());
		// entity.setEtime(TimeTool.getTimeDate14());
		// entity.setPstate("1");
		paperSubjectMapper.insertEntity(entity);
		return entity;
	}

	@Override
	@Transactional
	public PaperSubject editPapersubjectEntity(PaperSubject entity,
			LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		PaperSubject entity2 = paperSubjectMapper.getEntity(entity.getId());
		// entity2.setEuser(user.getId());
		// entity2.setEusername(user.getName());
		// entity2.setEtime(TimeTool.getTimeDate14());
		entity2.setPoint(entity.getPoint());
		entity2.setSort(entity.getSort());
		entity2.setChapterid(entity.getChapterid());
		entity2.setSubjectid(entity.getSubjectid());
		entity2.setVersionid(entity.getVersionid());
		entity2.setId(entity.getId());
		paperSubjectMapper.editEntity(entity2);
		return entity2;
	}

	@Override
	@Transactional
	public void deletePapersubjectEntity(String id, LoginUser user) {
		// TODO 自动生成代码,修改后请去除本注释
		paperSubjectMapper.deleteEntity(id);
	}

	@Override
	@Transactional
	public PaperSubject getPapersubjectEntity(String id) {
		// TODO 自动生成代码,修改后请去除本注释
		if (id == null) {
			return null;
		}
		return paperSubjectMapper.getEntity(id);
	}

	@Override
	@Transactional
	public DataQuery createPapersubjectSimpleQuery(DataQuery query) {
		DataQuery dbQuery = DataQuery
				.init(query,
						"WTS_PAPER_SUBJECT a left join WTS_PAPER_CHAPTER b on a.CHAPTERID=b.ID LEFT JOIN WTS_SUBJECT_VERSION c on a.VERSIONID=c.ID left join WTS_SUBJECT d on a.subjectid=d.id left join WTS_MATERIAL e on d.MATERIALID =e.id left join wts_subject_type f on f.id=d.typeid",
						"a.ID AS ID, a.POINT AS POINT, a.SORT AS SORT, c.TIPTYPE AS TIPTYPE, c.TIPSTR AS TIPSTR,e.title as MTITLE,f.name as TYPENAME");
		return dbQuery;
	}

	@Override
	@Transactional
	public PaperSubject getPapersubjectEntity(String paperid, String chapterid,
			String subjectid) {
		List<PaperSubject> subjects = paperSubjectMapper.findByChapteridAndPaperIdAndSubjectid(chapterid,paperid,subjectid);

		if (subjects.size() > 0) {
			return subjects.get(0);
		} else {
			return null;
		}
	}

}
