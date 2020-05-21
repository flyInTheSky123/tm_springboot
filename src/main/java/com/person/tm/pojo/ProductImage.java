package com.person.tm.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;


//产品图片类
@Entity
@Table(name = "productimage")
//因为创建代理类会继承当前实体类，有handler，hibernateLazyInitializer两个参数。
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer"})
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int  id;

//jackson中的@JsonBackReference和@JsonManagedReference，
// 以及@JsonIgnore均是为了解决对象中存在双向引用导致的无限递归（infinite recursion）问题
//@JsonBackReference标注的属性在序列化（serialization，即将对象转换为json数据）时，会被忽略（即结果中的json数据不包含该属性的内容）
    @ManyToOne  //多对一
    @JoinColumn(name = "pid")
    @JsonBackReference
    private Product product;

    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
