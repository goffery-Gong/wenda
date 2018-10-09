package com.nowcoder.async;

import java.util.HashMap;
import java.util.Map;

//事件模型
public class EventModel {
	//事件类型
	private EventType type;
	//操作者id
	private int actorId;
	//对象id
	private int entityId;
	//对象属性
	private int entityType;
	//关联对象的id
	private int entityOwnerId;
	
	private Map<String, String> extr=new HashMap<>();

	public EventModel setExt(String key,String value){
		extr.put(key, value);
		return this;
	}
	
	public String getExt(String key){
		return extr.get(key);
	}
	
	public EventType getType() {
		return type;
	}

	public EventModel setType(EventType type) {
		this.type = type;
		return this;
	}

	public int getActorId() {
		return actorId;
	}

	public EventModel setActorId(int actorId) {
		this.actorId = actorId;
		return this;
	}

	public int getEntityId() {
		return entityId;
	}

	public EventModel setEntityId(int entityId) {
		entityId = entityId;
		return this;
	}

	public int getEntityType() {
		return entityId;
	}

	public EventModel setEntityType(int entityType) {
		entityId = entityType;
		return this;
	}

	public Map<String, String> getExtr() {
		return extr;
	}

	public void setExtr(Map<String, String> extr) {
		this.extr = extr;
	}
	
	
}
