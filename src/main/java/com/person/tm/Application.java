package com.person.tm;

import com.person.tm.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//启动类
@SpringBootApplication
//用于启动缓存c
@EnableCaching
//elasticsearch
@EnableElasticsearchRepositories(basePackages = "com.person.tm.es")
//springboot 用于链接elasticsearch的jpa
@EnableJpaRepositories(basePackages = {"com.person.tm.dao", "com.person.tm.pojo"})
public class Application {
    static {
        //用于检测该 端口是否已经启动。
        //检查端口6379是否启动。 6379 就是 Redis 服务器使用的端口。如果未启动，那么就会退出 springboot。
        PortUtil.checkPort(6379, "redis 服务端 ", true);
    }


    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
