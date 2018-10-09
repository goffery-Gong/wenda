package com.nowcoder.async;

import java.util.List;

public interface EventHandler {
	//事件处理方法
	void doHandler(EventModel model);
	
	//获得此handler所处理的事件类型
	List<EventType> getSupportEventTypes();
}
