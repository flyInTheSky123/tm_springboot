package com.person.tm.dao;

import com.person.tm.pojo.Category;
import com.person.tm.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;

import java.util.List;


public interface PropertyDAO extends JpaRepository<Property ,Integer> {

    //通过category 查询 property。pageable 支持分页。
    Page<Property> findByCategory(Category category, Pageable pageable);
    //通过category获取相应的property
    List<Property> findByCategory(Category category);
}
