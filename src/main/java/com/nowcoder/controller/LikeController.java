package com.nowcoder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.LikeService;
import com.nowcoder.utils.WendaUtil;

public class LikeController {
	@Autowired
	LikeService likeService;

	@Autowired
	HostHolder hostholder;

	@RequestMapping(path = { "/like" }, method = { RequestMethod.POST })
	@ResponseBody
	public String addComment(@RequestParam("commontId") int commontId) {
		if (hostholder.getUser() != null)
			return WendaUtil.getJSONString(999);
		long likeCount = likeService.like(hostholder.getUser().getId(),
				EntityType.ENTITY_COMMENT, commontId);
		return WendaUtil.getJsonString(0, String.valueOf(likeCount));
	}
}
