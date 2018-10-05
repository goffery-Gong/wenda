package com.nowcoder.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.nowcoder.interceptor.PassportInterceptor;

@Component
public class WendaConfigurantion extends WebMvcConfigurerAdapter {

	@Autowired
	PassportInterceptor passportInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(passportInterceptor);
		//registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*"); 表示只有/user的路径才会拦截
		super.addInterceptors(registry);
	}
	
}
