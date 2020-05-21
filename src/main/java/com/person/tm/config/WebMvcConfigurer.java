package com.person.tm.config;

import com.person.tm.interceptor.LoginInterceptor;
import com.person.tm.interceptor.OtherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

//登录拦截器配置适配器
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {


    //interceptor 对象
    @Bean
    public LoginInterceptor getLoginIntercepter(){
        return new LoginInterceptor();
    }


    //其它拦截器
    @Bean
    public OtherInterceptor getOtherIntercepter(){
        return new OtherInterceptor();
    }

    //为指定的拦截器 指定拦截路径
    @Override
    public void  addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(getLoginIntercepter()).addPathPatterns("/**");
        registry.addInterceptor(getOtherIntercepter()).addPathPatterns("/**");
    }



}
