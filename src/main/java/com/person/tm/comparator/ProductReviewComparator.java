package com.person.tm.comparator;

import com.person.tm.pojo.Product;

import java.util.Comparator;

//产品人气比较器。把评论数量多的产品放在前面
public class ProductReviewComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getReviewCount()-p1.getReviewCount();
    }
}
