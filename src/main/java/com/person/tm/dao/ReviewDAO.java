package com.person.tm.dao;

import com.person.tm.pojo.Product;
import com.person.tm.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewDAO extends JpaRepository<Review,Integer> {

    //通过pid查找数据
    List<Review> findByProductOrderByIdDesc(Product product);


    //计算该产品有多少评价数量。
    int  countByProduct(Product product);





}
