package com.person.tm.pojo;

import com.carrotsearch.hppc.HashOrderMixing;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.internal.NotNull;
import org.springframework.data.elasticsearch.annotations.Document;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "product")
@Document(indexName = "tmall_springboot",type = "product")
@JsonIgnoreProperties({"handler","hibernateLazyInitializer"})
public class Product {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) //id 自动增长
    @Column(name = "id")
    private  int id;


    @ManyToOne
    @JoinColumn(name = "cid")
    private Category category;

    //如果既没有指明 关联到哪个Column,又没有明确要用@Transient忽略，那么就会自动关联到表对应的同名字段
    private String name;
    private String subTitle;
    private float originalPrice;
    private float promotePrice;
    private int stock;
    private Date createDate;


//    @transient 就是在给某个javabean上需要添加个属性，
//    但是这个属性你又不希望给存到数据库中去，仅仅是做个临时变量，用一下。不修改已经存在数据库的数据的数据结构。
    @Transient  //忽略当前属性
    private  ProductImage firstProductImage;


    //产品单个图片
    @Transient
    private List<ProductImage> productSingleImages;

    //产品详情图片
    @Transient
    private List<ProductImage> productDetailImages;

    //该产品的销售量
    @Transient
    private int saleCount;

    //该评价的数量
    @Transient
    private int reviewCount;
    public Product() {

    }

    public List<ProductImage> getProductSingleImages() {
        return productSingleImages;
    }

    public void setProductSingleImages(List<ProductImage> productSingleImages) {
        this.productSingleImages = productSingleImages;
    }

    public List<ProductImage> getProductDetailImages() {
        return productDetailImages;
    }

    public void setProductDetailImages(List<ProductImage> productDetailImages) {
        this.productDetailImages = productDetailImages;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public ProductImage getFirstProductImage() {
        return firstProductImage;
    }

    public void setFirstProductImage(ProductImage firstProductImage) {
        this.firstProductImage = firstProductImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public float getPromotePrice() {
        return promotePrice;
    }

    public void setPromotePrice(float promotePrice) {
        this.promotePrice = promotePrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", category=" + category +
                ", name='" + name + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", originalPrice=" + originalPrice +
                ", promotePrice=" + promotePrice +
                ", stock=" + stock +
                ", createDate=" + createDate +
                ", firstProductImage=" + firstProductImage +
                ", productSingleImages=" + productSingleImages +
                ", productDetailImages=" + productDetailImages +
                ", saleCount=" + saleCount +
                ", reviewCount=" + reviewCount +
                '}';
    }
}
