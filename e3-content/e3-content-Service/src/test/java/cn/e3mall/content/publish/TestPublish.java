//package cn.e3mall.content.publish;
//
//import org.junit.Test;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import cn.e3mall.content.service.ContentCategoryService;
//
////测试有问题导致服务启动不了，所以删掉了
//public class TestPublish {
//	static ApplicationContext applicationContext;
//	
//	static {
//		applicationContext=
//				new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");
//		System.out.println("服务已经启动");
//	}
//	
//	ContentCategoryService contentCategoryService;
//	
//	@Test
//	public void publishService() throws Exception{
//		contentCategoryService = (ContentCategoryService) applicationContext.getBean("contentCategoryServiceImpl");
//		contentCategoryService.deleteCategory((long)167);
//		//		contentCategoryService=(ContentCategoryService) beanFactory.getBean("sqlSessionFactory");
////		System.out.println(contentCategoryService.isParent((long) 169));;
////		System.out.println("服务器已经关闭");
//		
//	}
//}
