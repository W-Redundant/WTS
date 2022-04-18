package com.farm.wcp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.farm.core.page.ViewMode;
import com.farm.parameter.FarmParameterService;
import com.farm.web.WebUtils;

/**
 * 判卷
 * 
 * @author autoCode
 * 
 */
@RequestMapping("/demo")
@Controller
public class demoWebController extends WebUtils {

	public static String getThemePath() {
		return FarmParameterService.getInstance().getParameter("config.sys.web.themes.path");
	}

	/***
	 * 考场判卷首页
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/PubHome")
	public ModelAndView index(String roomid, HttpServletRequest request, HttpSession session) {
		try {
			ViewMode view = ViewMode.getInstance();
			return view.returnModelAndView("/demo/index");
		} catch (Exception e) {
			return ViewMode.getInstance().setError(e.getMessage(), e).returnModelAndView(getThemePath() + "/error");
		}
	}

}
