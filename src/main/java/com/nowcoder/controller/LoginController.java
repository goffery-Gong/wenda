package com.nowcoder.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.nowcoder.service.UserService;

@Controller
public class LoginController {
	private static final Logger logger = LoggerFactory
			.getLogger(LoginController.class);

	@Autowired
	UserService userService;

	/**
	 * 注册
	 * 
	 * @param model
	 * @param username
	 * @param password
	 * @param response
	 * @return
	 */
	@RequestMapping(path = "/reg/", method = { RequestMethod.POST })
	public String Reg(Model model, @RequestParam("username") String username,
			@RequestParam("password") String password,
			HttpServletResponse response) {
		try {
			Map<String, Object> map = userService.register(username, password);
			if (!map.containsKey("msg")) {
				Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
				cookie.setPath("/");
				response.addCookie(cookie);
			} else {
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
			// 如果正确就重定向到首页
			return "redirect:/";
		} catch (Exception e) {
			logger.error("注册异常" + e.getMessage());
			return "login";
		}
	}

	@RequestMapping(path = "/reglogin", method = {RequestMethod.GET })
	public String reg(Model model) {
		//model.addAttribute("next",next);
		return "login";
	}

	/**
	 * 登录
	 * 
	 * @param model
	 * @param username
	 * @param password
	 * @param response
	 * @return
	 */
	@RequestMapping(path = "/login/", method = { RequestMethod.POST })
	public String Login(
			Model model,
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam(value = "rememberme", defaultValue = "false") boolean rememberme,
			HttpServletResponse response) {
		try {
			Map<String, Object> map = userService.login(username, password);
			if (!map.containsKey("msg")) {
				// 成功登录就设置cookie
				Cookie cookie = new Cookie("ticket", (String) map.get("ticket"));
				cookie.setPath("/"); 
				if (rememberme)
					cookie.setMaxAge(3600 * 24 * 5);
				response.addCookie(cookie);
			} else {
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
			// 如果正确就重定向到首页
			return "redirect:/";
		} catch (Exception e) {
			logger.error("登录异常" + e.getMessage());
			return "login";
		}
	}
	
	/**
	 * 登出
	 * @param ticket
	 * @return
	 */
	@RequestMapping(path = "/logout", method = { RequestMethod.GET })
	public String logout(@CookieValue("ticket") String ticket) {
		userService.logout(ticket);
		return "redirect:/";	//一定要用重定向，不然缓存还在
	}

}
