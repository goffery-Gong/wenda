# wenda
zhihu
仿照知乎做的一个Java web项目，是一个sns+资讯的web应用。使用SpringBoot+Mybatis+velocity开发。数据库使用了redis和mysql，同时加入了异步消息等进阶功能，同时使用python爬虫进行数据填充。  
内容包括：  
	开发工具和Java语言介绍
	Spring入门，模板语法和渲染
	数据库交互iBatis集成
	用户注册登录管理
	问题发布，敏感词和js标签过滤，问题广场
	评论中心，站内信
	Redis入门以及Redis实现赞踩功能
	异步设计和站内邮件通知系统
	sns关注功能，关注和粉丝列表页实现
	timeline实现
	python语法简介，pip工具介绍
	python爬虫实现数据抓取和导入
	站内全文搜索
	项目测试和部署，课程总结回顾
	
## 基本框架开发
	创建基本的controller，service和model层。
	
	controller中使用注解配置，requestmapping，responsebody基本可以解决请求转发以及响应内容的渲染。responsebody自动选择viewresolver进行解析。
	
	使用pathvariable和requestparam传递参数。
	
	使用velocity编写页面模板，注意其中的语法使用。常用$!{}和${}
	
	使用http规范下的httpservletrequest和httpservletresponse来封装请求和相响应，使用封装好的session和cookie对象。
	
	使用重定向的redirectview和统一异常处理器exceptionhandler