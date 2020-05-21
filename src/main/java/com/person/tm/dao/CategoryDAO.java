package com.person.tm.dao;

import com.person.tm.pojo.Category;
import org.springframework.data.jpa.repository.JpaRepository;

//Jpa作为持久层
public interface CategoryDAO extends JpaRepository<Category,Integer> {

}
