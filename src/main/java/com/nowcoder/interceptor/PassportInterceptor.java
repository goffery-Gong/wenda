package com.nowcoder.interceptor;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;

@Component
public class PassportInterceptor implements HandlerInterceptor{

	@Autowired
	LoginTicketDAO loginTicketDAO;
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	HostHolder hostHolder;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		hostHolder.clear();
	}

	//posthandler在controller执行之后，渲染执行之前进行执行。
	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		if(arg3!=null&& hostHolder.getUser()!=null){
			arg3.addObject("user",hostHolder.getUser());
		}
	}
	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2) throws Exception {
		// 从cookie中获取ticket
		 String ticket=null;
		 if(arg0.getCookies()!=null){
			 for(Cookie cookie:arg0.getCookies()){
				 if(cookie.getName().equals("ticket")){
					 ticket=cookie.getValue();
					 break;
				 }
			 }
		 }
		 //通过ticket找到loginticket，再查到user;最后将user放到一个component
		 //中，便于所有后台通过spring能够取到user   
		 if(ticket!=null){
			 LoginTicket loginTicket=loginTicketDAO.selectByTicket(ticket);
			 if(loginTicket==null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus()!=0){
				 return true;
			 }
			 User user=userDAO.selectById(loginTicket.getUserId());
			 hostHolder.setUsers(user);
		 }
		return true ;
		
	}

}
