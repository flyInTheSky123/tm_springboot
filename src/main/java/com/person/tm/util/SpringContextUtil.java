package com.person.tm.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/*因为 springboot 的缓存机制是通过切面编程 aop来实现的。
从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
所以要通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存。

*/
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private SpringContextUtil() {
    }

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext){
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}