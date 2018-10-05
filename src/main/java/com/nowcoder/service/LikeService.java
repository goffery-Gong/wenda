package com.nowcoder.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.nowcoder.utils.JedisAdaptor;
import com.nowcoder.utils.RedisKeyUtil;

public class LikeService {
	@Autowired
	JedisAdaptor jedisAdaptor;
	
	public long getLikeCount(int entityType,int entityId){
		String likeKey=RedisKeyUtil.getLikeKey(entityType, entityId);
		return jedisAdaptor.scard(likeKey);
	}
	
	public int getLikeStatus(int userId, int entityType,int entityId){
		String likeKey=RedisKeyUtil.getLikeKey(entityType, entityId);
		if(jedisAdaptor.sismember(likeKey, String.valueOf(userId)))
			return 1;
		String disLikeKey=RedisKeyUtil.getDisLikeKey(entityType, entityId);
		return jedisAdaptor.sismember(disLikeKey, String.valueOf(userId))?-1:0;
	}
	
	public long like (int userId,int entityType,int entityId){
		String likeKey=RedisKeyUtil.getLikeKey(entityType, entityId);
		jedisAdaptor.sadd(likeKey, String.valueOf(userId));
		
		String disLikeKey=RedisKeyUtil.getDisLikeKey(entityType, entityId);
		jedisAdaptor.srem(disLikeKey,String.valueOf(userId));
		
		return jedisAdaptor.scard(likeKey);
	}
	
	public long dislike (int userId,int entityType,int entityId){
		String dislikeKey=RedisKeyUtil.getLikeKey(entityType, entityId);
		jedisAdaptor.sadd(dislikeKey, String.valueOf(userId));
		
		String likeKey=RedisKeyUtil.getDisLikeKey(entityType, entityId);
		jedisAdaptor.srem(likeKey,String.valueOf(userId));
		
		return jedisAdaptor.scard(likeKey);
	}
}
