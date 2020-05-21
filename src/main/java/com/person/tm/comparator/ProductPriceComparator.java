package com.person.tm.comparator;

import com.person.tm.pojo.Product;

import java.util.Comparator;

//产品价格比较器。把价格低的产品放在前面
public class ProductPriceComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return (int)(p1.getPromotePrice()-p2.getPromotePrice());
    }
}
