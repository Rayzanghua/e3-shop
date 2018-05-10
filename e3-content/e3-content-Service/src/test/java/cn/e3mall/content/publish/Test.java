//package cn.e3mall.content.publish;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//import cn.e3mall.content.service.ContentCategoryService;
//
//public class Test {  
//    public static void main(String args[]) {  
//        Test test = new Test();  
//        test.test();  
//    }  
//  
//    public void test() {  
//        ApplicationContext context = BeanContextHelper.getApplicationContext();  
//        ContentCategoryService bean = (ContentCategoryService) context.getBean("contentCategoryServiceImpl");  
//    }  
//} 
//class BeanContextHelper {  
//    private static ApplicationContext _instance;  
//  
//    static {  
//        if (_instance == null)  
//            _instance = buildApplicationContext();  
//    }  
//  
//    private BeanContextHelper() {  
//    }  
//  
//    /** 
//     * 重新构建ApplicationContext对象  
//     */  
//    public static ApplicationContext buildApplicationContext() {  
//        return new ClassPathXmlApplicationContext("classpath:spring/applicationContext-*.xml");  
//    }  
//  
//    /** 
//     * 获取一个ApplicationContext对象 
//     */  
//    public static ApplicationContext getApplicationContext() {  
//        return _instance;  
//    }  
//} 