package com.nowcoder.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.nowcoder.utils.JedisAdaptor;
import com.nowcoder.utils.RedisKeyUtil;

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware {

	private static final Logger logger = LoggerFactory
			.getLogger(EventConsumer.class);
	// 存放某种事件类型对应的handler
	private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
	private ApplicationContext applicationContext;

	@Autowired
	JedisAdaptor jedisAdaptor;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 通过applicationcontext获得工程中所有的实现了eventhandle接口的handler实现类
		Map<String, EventHandler> beans = applicationContext
				.getBeansOfType(EventHandler.class);
		if (beans != null) {
			// 遍历handler，获得其支持的多种事件类型；然后将每一种事件类型和对应的handler放在一个map中（config）。
			// 当处理某个event时，就可以通过config得到其处理方法的list（List<EventHandler>）
			for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
				List<EventType> eventTypes = entry.getValue().getSupportEventTypes();

				for (EventType type : eventTypes) {
					if (!config.containsKey(type))
						config.put(type, new ArrayList<EventHandler>());

					config.get(type).add(entry.getValue());
				}
			}
		}

		//启动线程，将redis中的eventModel取出来，得到其事件类型；然后在config中进行匹配对应的handler
		//如果识别了，则调用handler.doHandler()进行处理。
		new Thread(() -> {
			while (true) {
				String key = RedisKeyUtil.getEventQueueKey();
				List<String> events = jedisAdaptor.brpop(0, key);

				for (String message : events) {
					if (message.equals(key))
						continue;

					// 将eventModel解析出来，便于得到其中存放的事件类型：EventType
					EventModel eventModel = JSON.parseObject(message,
							EventModel.class);
					if (!config.containsKey(eventModel.getType())) {
						logger.error("不能识别的事件");
						continue;
					}
	
					for (EventHandler handler : config.get(eventModel.getType()))
						handler.doHandler(eventModel);
				}
			}
		}).start();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext=applicationContext;
	}
	
	
}
