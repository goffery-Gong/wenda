package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
	//使用多线程的user，防止产生并发问题
	ThreadLocal<User> users=new ThreadLocal<>();

	//获取当前线程的user值
	public User getUser() {
		return users.get();
	}

	//设置新的值
	public void setUsers(User user) {
		users.set(user);
	}
	
	//清除当前线程的user值
	public void clear(){
		users.remove();
	}
	
}
