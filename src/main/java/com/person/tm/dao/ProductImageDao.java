package com.person.tm.dao;

import com.person.tm.pojo.Product;
import com.person.tm.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//产品图片DAO 继承Jpa
public interface ProductImageDao extends JpaRepository<ProductImage,Integer> {
    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product  product,String type);

}
