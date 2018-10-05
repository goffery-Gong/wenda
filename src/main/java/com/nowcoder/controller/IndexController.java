package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.User;
import com.nowcoder.service.WendaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/10.
 */
//@Controller
public class IndexController { 
	@RequestMapping(path = { "/", "/index" })
	@ResponseBody
	public ModelAndView index(ModelAndView mv) {
		mv.setViewName("index");
		return mv;
	}

	//2.获取路径中的参数@PathVariable；请求参数传递@RequestParam
	@RequestMapping(path = { "/profile/{groupName}/{userId}" })
	@ResponseBody
	public String profile(@PathVariable("userId") int userId,
						  @PathVariable("groupName") String groupName,
						  @RequestParam(value="type",defaultValue="1",required=false) int type,
						  @RequestParam(value="key",defaultValue="",required=false) String key) {
		return String.format("Profile Page of %s/%d, type:%d key:%s", groupName, userId,type,key);
	}
	
	@RequestMapping(path = { "/vm"})
	public String template(Model model) {
		List<String> list=Arrays.asList(new String[]{"yellow","red","gray"});
		model.addAttribute("colors", list);
		model.addAttribute("value", "ffffff");
		
		Map<String, String> map=new HashMap<>();
		for(int i=0;i<5;i++)
			map.put(String.valueOf(i), String.valueOf(i*i));
		model.addAttribute("map", map);
		model.addAttribute("user", new User("mmd"));
		return "home";
	}
}
