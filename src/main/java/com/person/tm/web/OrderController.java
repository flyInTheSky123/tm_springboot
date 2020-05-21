package com.person.tm.web;

import com.person.tm.pojo.Order;
import com.person.tm.service.OrderItemService;
import com.person.tm.service.OrderService;
import com.person.tm.util.Page4Navigator;
import com.person.tm.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderItemService orderItemService;
//
//    //获取订单
//    @GetMapping("/orders")
//    public Page4Navigator<Order> list(@RequestParam(name = "start" ,defaultValue = "0") int start,
//                                      @RequestParam(name = "size" ,defaultValue = "5") int size){
//
//        //获取订单
//        Page4Navigator<Order> page = orderService.list(start>0?start:0, size, 5);
//        //为每个订单设置订单项和产品图片
//        orderItemService.fill(page.getContent());
//        //为了防止无限循环，把orderitem里面的订单属性置空
//        // （因为 对order 进行json 化时，会对里面的orderitem json化，orderitem里面又有 order 又会json化......一直循环）
//        orderService.removeOrderFromOrderItem(page.getContent());
//        return page;
//    }


    @GetMapping("/orders")
    public Page4Navigator<Order> list(@RequestParam(value = "start", defaultValue = "0") int start,@RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start<0?0:start;
        Page4Navigator<Order> page =orderService.list(start, size, 5);
        orderItemService.fill(page.getContent());
        orderService.removeOrderFromOrderItem(page.getContent());
        return page;
    }


    //发货,更新order订单
    @PutMapping("deliveryOrder/{oid}")
    public Object DeliverOrder(@PathVariable int oid){
        Order one = orderService.getOne(oid);
        one.setDeliveryDate(new Date());
        //设置状态
        one.setStatus(OrderService.waitConfirm);
        orderService.update(one);
        return Result.success();

    }



}
