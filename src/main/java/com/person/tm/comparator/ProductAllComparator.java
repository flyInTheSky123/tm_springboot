package com.person.tm.comparator;

import com.person.tm.pojo.Product;

import java.util.Comparator;

//产品综合比较器。把product 的 销售量*评价量 最高的放在前面
public class ProductAllComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return  p2.getSaleCount()*p2.getReviewCount()-p1.getSaleCount()*p1.getReviewCount();
    }
}
