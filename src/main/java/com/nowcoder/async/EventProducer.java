package com.nowcoder.async;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.utils.JedisAdaptor;
import com.nowcoder.utils.RedisKeyUtil;

@Service
public class EventProducer {
	@Autowired
	JedisAdaptor jedisAdaptor;

	public boolean fireEvent(EventModel eventModel) {
		// 使用redis实现队列
		try {
			//可以采用阻塞队列来实现异步队列
			//BlockingQueue<EventModel> q=new ArrayBlockingQueue<>(10);
			
			//生产者队列
			String json=JSONObject.toJSONString(eventModel);
			String key=RedisKeyUtil.getEventQueueKey();
			jedisAdaptor.lpush(key, json);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
