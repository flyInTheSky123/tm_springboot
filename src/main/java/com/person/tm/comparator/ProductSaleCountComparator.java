package com.person.tm.comparator;

import com.person.tm.pojo.Product;

import java.util.Comparator;

//产品销售比较器。把销售数量多的产品放在前面
public class ProductSaleCountComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount()-p1.getSaleCount();
    }
}
