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
	
## 数据库配置和首页的创建
	使用mysql创建数据库和表，建议自己写一下sql到mysql命令行跑一下。
	
	加入mybatis和mysql的maven仓库，注意，由于现在版本的springboot不再支持velocity进而导致我使用较早版本的springboot，所以这里提供一可以正常运行的版本设置。
	
	springboot使用1.3.6
	
	mybatis-spring-boot-starter使用1.1.1
	
	mysql-connector-java使用5.1.39
	
	亲测可用。
	
	接下来写controller，dao和service。注意mybatis的注解语法以及xml的配置要求，xml要求放在resource中并且与dao接口在相同的包路径下。
	
	application.properties增加spring配置数据库链接地址
	
	两个小工具：
	ViewObject:方便传递任何数据到
	VelocityDateTool:velocity自带工具类（在前端页面显示日期格式：）	
		1. 在resource中加入toolbox.xml配置文件
		2. 在application.propertis中加入配置：spring.velocity.toolbox-config-location=toolbox.xml
		3. 在前端页面中使用$date.format('yyyy-MM-dd HH:mm:ss', $!{vo.question.createdDate})来更改日期格式；

	写好静态文件html css和js。并且注意需要配置
	spring.velocity.suffix=.html 保证跳转请求转发到html上
	spring.velocity.toolbox-config-location=toolbox.xml
	
	至此主页基本完成，具体业务逻辑请参考代码。
	
## 用户注册登录以及使用token
	通过coolie、ticket和Interceptor实现将用户登录信息保存在cookie中，可以保持登录状态而访问页面。（详细请见：loginController、UserService。）
	* 新建数据表login_ticket用来存储ticket字段。该字段在用户登录成功时被生成并存入数据库，（设定其id与失效时间），将ticket值放入map中返回controller
	* controller中设置cookie("ticket”,map,get("ticket"));下次用户登录时会带上这个ticket，ticket是随机的uuid，有过期时间以及有效状态。
	* 拦截所有用户请求，判断请求中是否有有有效的ticket：在interceptor中获取cookie中的ticket，通过ticket找到loginticket，再查到user;最后将user放到一个component对象——hostholder（Threadlocal）中，便于所有后台通过spring能够取到user   
	* 配置了用户的Wendaconfiguration来设置启动时的配置，这里可以将上述的两个拦截器加到启动项里。
	* 通过清除ticket来实现登出
	* 思考：个人觉得没必要将ticket专门建表存放；可以只在user中加一个字段ticket就行，这样方便在interceptor中通过ticket取到user
	数据安全性的保障手段：https使用公钥加密私钥解密，比如支付宝的密码加密，单点登录验证，验证码机制等。

## 问题发表功能（敏感词过滤）
防止xss注入直接使用HTMLutils的方法即可实现。
通过创建字典树，并进行查找比较，实现敏感词过滤  
- [字典树实现敏感词过滤](https://www.jianshu.com/p/52709faef79c)  
![敏感词过滤流程图](https://github.com/goffery-Gong/wenda/tree/master/src/main/resources/敏感词过滤.jpg)

## 点赞和点踩功能，使用Redis实现
	1.开发点踩和点赞功能，在此之前根据业务封装好jedis的增删改查操作，放在util包中。使用JedisPool连接池初始化连接。
	其中：通过实现InitializingBean接口来进行类的初始化
	
	@Service
	public class JedisAdaptor implements InitializingBean{
		private JedisPool pool;
		private static final Logger logger=LoggerFactory.getLogger(JedisAdaptor.class);
		
		@Override
		//初始化连接池
		public void afterPropertiesSet() throws Exception {
			pool=new JedisPool("redis://localhost:6379/10");
		}
	}
	2.根据需求确定key字段，格式是 like：实体类型：实体id 和 dislike：实体类型：实体id  
	这样可以将喜欢一条新闻的人存在一个集合，不喜欢的存在另一个集合。通过统计数量可以获得点赞和点踩数。
	
	3.一般点赞点踩操作是先修改redis的值并获取返回值，然后再异步修改mysql数据库的likecount数值。  
	这样既可以保证点赞操作快速完成，也可保证数据一致性。

##	异步队列
-通过redis实现异步队列。
-EventConsumer中实现了ApplicationContextAware接口，可以在Spring初始化实例 Bean的时候，可以通过这个接口将当前的Spring上下文传入。  
	加载Spring配置文件时，如果Spring配置文件中所定义的Bean类实现了ApplicationContextAware 接口，那么在加载Spring配置文件时，会自动调用ApplicationContextAware 接口中的
	public void setApplicationContext(ApplicationContext context) throws BeansException方法，获得ApplicationContext 对象。
	前提必须在Spring配置文件中使用@Component注册该类。
	@Component
	public class ApplicationContextHelper implements ApplicationContextAware {
		//以静态变量保存ApplicationContext,可在任意代码中取出ApplicaitonContext. 
	    private static ApplicationContext applicationContext;

	    //实现ApplicationContextAware接口的context注入函数, 将其存入静态变量. 
	    @Override
	    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	          ApplicationContextHelper.applicationContext = applicationContext;
	    }
		
	 	//从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型. 
	    public static <T> T getBean(Class<T> clazz){
	          return applicationContext.getBean(clazz);
	    }
	}

	
	
	
	
	
	
	
	