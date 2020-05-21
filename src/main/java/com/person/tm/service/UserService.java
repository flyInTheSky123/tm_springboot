package com.person.tm.service;

import com.person.tm.dao.UserDao;
import com.person.tm.pojo.User;
import com.person.tm.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;



@Service
@CacheConfig(cacheNames="users")
public class UserService {
    @Autowired
    UserDao userDao;

    //显示用户
    @Cacheable(key="'users-page-'+#p0+ '-' + #p1")
    public Page4Navigator<User> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page all = userDao.findAll(pageable);

        return new Page4Navigator<>(all, navigatePages);

    }

    //保存user
    @CacheEvict(allEntries = true)
    public void add(User user){
    userDao.save(user);
    }


    //通过用户名称查到用户
    @Cacheable(key="'users-one-name-'+ #p0")
    public User getByName(String name){
        return userDao.findByName(name);
    }

    //判断该用户是否存在
    public Boolean isExist(String name){
        User byName = getByName(name);
        return null!=byName;

    }

    //通过name/password判断进行登录
    @Cacheable(key="'users-one-name-'+ #p0 +'-password-'+ #p1")
    public User getByNameAndPassowrd(String name,String password){
        User user = userDao.getByNameAndPassword(name, password);
        return user;

    }








}



