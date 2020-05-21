package com.person.tm.service;

import com.person.tm.dao.OrderDAO;
import com.person.tm.pojo.Order;
import com.person.tm.pojo.OrderItem;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames="orders")
public class OrderService {
    @Autowired
    OrderDAO orderDAO;

    @Autowired
    OrderItemService orderItemService;

    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";


    //显示所有

    @Cacheable(key="'orders-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA,navigatePages);
    }

    /**
     * 因为SpringMVC ( springboot 里内置的mvc框架是 这个东西)的 RESTFUL 注解，在把一个Order转换为json的同时，
     * 会把其对应的 orderItems 转换为 json数组， 而 orderItem对象上有 order属性，
     * 这个order 属性又会被转换为 json对象，然后这个 order 下又有 orderItems 。。。
     * 就这样就会产生无穷递归，系统就会报错了。
     **/
    //置空订单属性
    public void removeOrderFromOrderItem(List<Order> orders) {


        for (Order order : orders) {
            removeOrderFromOrderItem(order);

        }
    }

    //置空订单属性
    public void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();

        orderItems.forEach(orderItem -> {
            orderItem.setOrder(null);
        });

    }

    //通过id 获取相应的信息。
    @Cacheable(key="'orders-one-'+ #p0")
    public Order getOne(int id) {
        Order order = orderDAO.getOne(id);
        return order;
    }

    //更新
    @CacheEvict(allEntries = true)
    public void update(Order bean) {
        orderDAO.save(bean);
    }


    //向订单项添加订单项
    //事务,如果出现exception,则事务回滚
    @CacheEvict(allEntries=true)
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public float add(Order order, List<OrderItem> orderItems) {
        float total = 0;

        //新增该订单
        add(order);

        //
        if (false) {
            throw new RuntimeException();
        }
        for (OrderItem oi : orderItems) {
            oi.setOrder(order);
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            orderItemService.update(oi);
        }
        return total;
    }

    @CacheEvict(allEntries = true)
    public void add(Order order) {
        orderDAO.save(order);
    }


    //查找user下 订单状态不是delete的order 并且为该订单设置
    public List<Order> listByUserWithoutDelete(User user) {
        // 查找user下 订单状态不是delete的order
        List<Order> orders = listByUserAndNotDelete(user);

        //为订单填充 数据和orderitem.
        orderItemService.fill(orders);
        return orders;


    }

    //查找user下 订单状态不是delete的order
    @Cacheable(key="'orders-uid-'+ #p0.id")
    public List<Order> listByUserAndNotDelete(User user) {
        return orderDAO.findByUserAndStatusNotOrderByIdDesc(user, OrderService.delete);
    }


    //获取 该订单的总付款金额
    public  void  getTotalMoney(Order order){
        float total=0;
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem oi:orderItems){
            total+=oi.getProduct().getPromotePrice()*oi.getNumber();
        }
        order.setTotal(total);

    }

 }
