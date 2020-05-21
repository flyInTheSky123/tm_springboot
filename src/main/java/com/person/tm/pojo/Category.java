package com.person.tm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

//Jpa作为持久层
@Entity
@Table(name = "category")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer" })
public class Category {


    @Id   //表明它是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //表明该字段自动的增长
    @Column(name = "id")             //对应表中的 id 一列
            int id;

    @Column(name = "name")
    String name;

//——————————————前端——————————————————————————————————
    //一个分类有多个产品
    @Transient
    List<Product> products;

    //一个分类有多行产品，每一行产品又有多个产品记录
    @Transient
    List<List<Product>> productsByRow;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }

    public void setProductsByRow(List<List<Product>> productsByRow) {
        this.productsByRow = productsByRow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
