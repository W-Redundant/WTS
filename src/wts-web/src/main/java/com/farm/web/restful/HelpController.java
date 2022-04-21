package com.farm.web.restful;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.farm.authority.service.OrganizationServiceInter;
import com.farm.authority.service.UserServiceInter;
import com.farm.core.page.ViewMode;
import com.farm.web.WebUtils;

/**
 * 组织机构、 [创建、查询、更新、删除] 用户、 [创建、查询、更新、删除] ---------------------------- 知识接口[查询]、
 * 分类接口[查询]、 问答接口[查询]、
 * 
 * @author wangdong
 *
 */
@RequestMapping("/helper")
@Controller
public class HelpController extends WebUtils {
	private final static Logger log = Logger.getLogger(HelpController.class);
	@Autowired
	private OrganizationServiceInter organizationServiceImpl;
	@Autowired
	private UserServiceInter userServiceImpl;

	/**
	 * API说明文档
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/readme")
	public ModelAndView index(HttpSession session, HttpServletRequest request) {
		String url = request.getRequestURL().toString().substring(0,
				request.getRequestURL().toString().lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url=url+"/api";
		return ViewMode.getInstance().putAttr("CURL", url).returnModelAndView("help/restfulApi");
	}
}
