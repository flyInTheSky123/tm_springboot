package com.person.tm.dao;

import com.person.tm.pojo.Order;
import com.person.tm.pojo.OrderItem;
import com.person.tm.pojo.Product;
import com.person.tm.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemDAO extends JpaRepository<OrderItem,Integer> {

    List<OrderItem> findByOrderOrderByIdDesc(Order order);
    List<OrderItem> findByProduct(Product product);
    //查询该用户下的，还没有支付的订单项
    List<OrderItem> findByUserAndOrderIsNull(User user);

}
