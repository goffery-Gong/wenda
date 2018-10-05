package com.nowcoder.utils;

public class RedisKeyUtil {
	private static String SPLIT=":";
	private static String BIZ_LIKE=":";
	private static String BIZ_DISLIKE=":";
	
	public static String getLikeKey(int entityType,int entityId){
		return BIZ_LIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
	}
	public  static String getDisLikeKey(int entityType,int entityId){
		return BIZ_DISLIKE+SPLIT+String.valueOf(entityType)+SPLIT+String.valueOf(entityId);
	}
}	
