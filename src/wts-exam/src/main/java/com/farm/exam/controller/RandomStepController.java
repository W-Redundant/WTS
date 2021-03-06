package com.farm.exam.controller;

import com.farm.exam.domain.RandomStep;
import com.farm.exam.domain.SubjectType;
import com.farm.exam.service.RandomStepServiceInter;
import com.farm.exam.service.SubjectTypeServiceInter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.annotation.Resource;
import com.farm.web.easyui.EasyUiUtils;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import javax.servlet.http.HttpSession;
import com.farm.core.page.RequestMode;
import com.farm.core.page.OperateType;
import com.farm.core.sql.query.DataQuery;
import com.farm.core.sql.query.DataQuerys;
import com.farm.core.sql.result.DataResult;
import com.farm.core.page.ViewMode;
import com.farm.web.WebUtils;

/* *
 *功能：答卷随机步骤控制层
 *详细：
 *
 *版本：v0.1
 *作者：FarmCode代码工程
 *日期：20150707114057
 *说明：
 */
@RequestMapping("/randomstep")
@Controller
public class RandomStepController extends WebUtils {
	private final static Logger log = Logger
			.getLogger(RandomStepController.class);
	@Autowired
	private RandomStepServiceInter randomStepServiceImpl;
	@Autowired
	private SubjectTypeServiceInter subjectTypeServiceImpl;

	/**
	 * 查询结果集合
	 * 
	 * @return
	 */
	@RequestMapping("/query")
	@ResponseBody
	public Map<String, Object> queryall(DataQuery query, String itemids,
			HttpServletRequest request) {
		try {
			query.addSqlRule(" and ITEMID in ("
					+ DataQuerys.parseSqlValues(parseIds(itemids)) + ")");
			query = EasyUiUtils.formatGridQuery(request, query);
			DataResult result = randomStepServiceImpl
					.createRandomstepSimpleQuery(query).search();
			result.runDictionary("1:填空,2:单选,3:多选,4:判断,5:问答,6:附件 ", "TIPTYPE");
			return ViewMode.getInstance()
					.putAttrs(EasyUiUtils.formatGridData(result))
					.returnObjMode();
		} catch (Exception e) {
			log.error(e.getMessage());
			return ViewMode.getInstance().setError(e.getMessage(), e)
					.returnObjMode();
		}
	}

	/**
	 * 提交修改数据
	 * 
	 * @return
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public Map<String, Object> editSubmit(RandomStep entity, HttpSession session) {
		// TODO 自动生成代码,修改后请去除本注释
		try {
			entity = randomStepServiceImpl.editRandomstepEntity(entity,
					getCurrentUser(session));
			return ViewMode.getInstance().setOperate(OperateType.UPDATE)
					.putAttr("entity", entity).returnObjMode();

		} catch (Exception e) {
			log.error(e.getMessage());
			return ViewMode.getInstance().setOperate(OperateType.UPDATE)
					.setError(e.getMessage(), e).returnObjMode();
		}
	}

	/**
	 * 提交新增数据
	 * 
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public Map<String, Object> addSubmit(RandomStep entity, HttpSession session) {
		try {
			List<String> itemids = parseIds(entity.getItemid());
			for (String itemid : itemids) {
				entity.setItemid(itemid);
				RandomStep newEntity = new RandomStep();
				BeanUtils.copyProperties(newEntity, entity);
				randomStepServiceImpl.insertRandomstepEntity(newEntity,
						getCurrentUser(session));
			}
			return ViewMode.getInstance().setOperate(OperateType.ADD)
					.returnObjMode();
		} catch (Exception e) {
			log.error(e.getMessage());
			return ViewMode.getInstance().setOperate(OperateType.ADD)
					.setError(e.getMessage(), e).returnObjMode();
		}
	}

	/**
	 * 删除数据
	 * 
	 * @return
	 */
	@RequestMapping("/del")
	@ResponseBody
	public Map<String, Object> delSubmit(String ids, HttpSession session) {
		try {
			for (String id : parseIds(ids)) {
				randomStepServiceImpl.deleteRandomstepEntity(id,
						getCurrentUser(session));
			}
			return ViewMode.getInstance().returnObjMode();
		} catch (Exception e) {
			log.error(e.getMessage());
			return ViewMode.getInstance().setError(e.getMessage(), e)
					.returnObjMode();
		}
	}

	@RequestMapping("/list")
	public ModelAndView index(HttpSession session) {
		return ViewMode.getInstance().returnModelAndView(
				"exam/RandomstepResult");
	}

	/**
	 * 显示详细信息（修改或浏览时）
	 *
	 * @return
	 */
	@RequestMapping("/form")
	public ModelAndView view(RequestMode pageset, String ids, String itemids) {
		try {
			switch (pageset.getOperateType()) {
			case (0): {// 查看
				return ViewMode
						.getInstance()
						.putAttr("pageset", pageset)
						.putAttr("entity",
								randomStepServiceImpl.getRandomstepEntity(ids))
						.returnModelAndView("exam/RandomstepForm");
			}
			case (1): {// 新增
				return ViewMode.getInstance().putAttr("pageset", pageset)
						.putAttr("itemids", itemids)
						.returnModelAndView("exam/RandomstepForm");
			}
			case (2): {// 修改
				RandomStep step = randomStepServiceImpl
						.getRandomstepEntity(ids);
				SubjectType type = subjectTypeServiceImpl
						.getSubjecttypeEntity(step.getTypeid());
				return ViewMode.getInstance().putAttr("pageset", pageset)
						.putAttr("typename", type.getName())
						.putAttr("entity", step)
						.returnModelAndView("exam/RandomstepForm");
			}
			default:
				break;
			}
			return ViewMode.getInstance().returnModelAndView(
					"exam/RandomstepForm");
		} catch (Exception e) {
			return ViewMode.getInstance().setError(e + e.getMessage(), e)
					.returnModelAndView("exam/RandomstepForm");
		}
	}
}
