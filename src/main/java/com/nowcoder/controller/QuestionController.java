package com.nowcoder.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Question;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.SensitiveService;
import com.nowcoder.utils.WendaUtil;

@Controller 
public class QuestionController {
	private static final Logger logger = LoggerFactory
			.getLogger(QuestionController.class);
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	HostHolder hostholder;
	
	/**
	 * ajax应用(问题发布)通过json将数据传输到前端
	 * @param title
	 * @param content
	 * @return
	 */
	@RequestMapping(value="/question/add",method={RequestMethod.POST})
	@ResponseBody
	public String addQuestion(@RequestParam("title") String title,
							  @RequestParam("content") String content){
		Question question=new Question();
		question.setContent(content);
		question.setTitle(title);
		question.setCreatedDate(new Date());
		question.setCommentCount(0);
		if(hostholder.getUser()==null){
			question.setUserId(3);
		}else{
			 question.setUserId(hostholder.getUser().getId());  
		}
		if(questionService.addQuestion(question)>0)
			return WendaUtil.getJSONString(0);
		return WendaUtil.getJsonString(1, "失败");
	}
}
