package com.person.tm.dao;


import com.person.tm.pojo.Product;
import com.person.tm.pojo.Property;
import com.person.tm.pojo.PropertyValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//属性值表 dao
public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
    //通过产品id 查找
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    //通过产品id和属性id查找
    PropertyValue getByPropertyAndProduct(Property property, Product product);

    //通过ptid 属性id进行删除 属性值。
    void deleteByProperty(Property property);

  // void deletePropertyValueByPropertyId(int id);
    List<PropertyValue> findByProperty(Property property);


}