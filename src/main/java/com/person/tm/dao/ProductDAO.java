package com.person.tm.dao;


import com.person.tm.pojo.Category;
import com.person.tm.pojo.Product;
import com.person.tm.util.Page4Navigator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


//productDao
public interface ProductDAO extends JpaRepository<Product, Integer> {

    Page<Product> findByCategory(Category category, Pageable pageable);
//--------------------前端------------
    //通过category获取相应的产品
    List<Product> findByCategoryOrderById(Category category);

    //模糊查询
    public List<Product> findByNameLike(String name,Pageable pageable);

}
