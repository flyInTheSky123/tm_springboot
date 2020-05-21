package com.person.tm.comparator;

import com.person.tm.pojo.Product;

import java.util.Comparator;
//产品新旧比较器。把旧的产品放在前面
public class ProductDateComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return p1.getCreateDate().compareTo(p2.getCreateDate());
    }
}
