package com.person.tm.dao;

import com.person.tm.pojo.Order;
import com.person.tm.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDAO extends JpaRepository<Order,Integer> {

    //通过user显示订单状态不为delete的订单。
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user,String status);
}
