package com.person.tm.dao;

import com.person.tm.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User,Integer> {


    //通过用户名查到用户信息。
    User findByName(String name);

    //通过用户名/密码进行查找登录。
    User getByNameAndPassword(String name,String password);
}
