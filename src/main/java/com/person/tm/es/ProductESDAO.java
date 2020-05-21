package com.person.tm.es;

import com.person.tm.pojo.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductESDAO extends ElasticsearchRepository<Product, Integer> {
}
