package com.nowcoder.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.User;

@Service
public class JedisAdaptor implements InitializingBean{
	private JedisPool pool;
	private static final Logger logger=LoggerFactory.getLogger(JedisAdaptor.class);
	
	@Override
	//初始化连接池
	public void afterPropertiesSet() throws Exception {
		pool=new JedisPool("redis://localhost:6379/10");
	}
	
	//将k，v添加到redis中的包装类
	public Long sadd(String key,String value){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			return jedis.sadd(key, value);
		} catch (Exception e) {
			logger.error("发生异常"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(jedis!=null)
				jedis.close();
		}
		return (long) 0;
	}
	
	public long scard(String key){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			return jedis.scard(key);
		} catch (Exception e) {
			logger.error("发生异常"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(jedis!=null)
				jedis.close();
		}
		return (long) 0;
	}
	
	public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return 0;
    }
	
	public boolean sismember(String key,String value){
		Jedis jedis=null;
		try {
			jedis=pool.getResource();
			return jedis.sismember(key,value);
		} catch (Exception e) {
			logger.error("发生异常"+e.getMessage());
			e.printStackTrace();
		}finally{
			if(jedis!=null)
				jedis.close();
		}
		return false;
	}
	
	public static void print(int index, Object obj){
		System.out.println(String.format("%d, %s", index,obj.toString()));
	}
	
	public static void main(String[] args) {
		Jedis jedis=new Jedis("redis://localhost:6379/9");
		jedis.flushDB();
		
		jedis.set("hello", "world");
		print(1,jedis.get("hello"));
		jedis.rename("hello", "new Hello");
		
		//设置超时
		jedis.setex("hello2", 15, "jffjfjf");
		
		
		//通过redis和json序列化实现缓存
		User user=new User();
		user.setName("xxx");
		user.setPassword("ooo");
		user.setHeadUrl("a.png");
		user.setId(1);
		user.setSalt("salt");
		//存入redis中
		jedis.set("user1", JSONObject.toJSONString(user));
		//反序列化取出
		String value=jedis.get("user1");
		User user2=JSON.parseObject(value, User.class);
		
	}

}
