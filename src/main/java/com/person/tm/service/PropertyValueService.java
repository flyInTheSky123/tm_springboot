package com.person.tm.service;

import com.person.tm.dao.PropertyValueDAO;
import com.person.tm.pojo.Product;
import com.person.tm.pojo.Property;
import com.person.tm.pojo.PropertyValue;
import com.person.tm.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@CacheConfig(cacheNames="propertyValues")
public class PropertyValueService  {

    @Autowired PropertyValueDAO propertyValueDAO;
    @Autowired PropertyService propertyService;

    @CacheEvict(allEntries=true)
    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }

    public void init(Product product) {
        PropertyValueService propertyValueService = SpringContextUtil.getBean(PropertyValueService.class);

        List<Property> propertys= propertyService.listByCategory(product.getCategory());
        for (Property property: propertys) {
            PropertyValue propertyValue = propertyValueService.getByPropertyAndProduct(product, property);
            if(null==propertyValue){
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    @Cacheable(key="'propertyValues-one-pid-'+#p0.id+ '-ptid-' + #p1.id")
    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property,product);
    }

    @Cacheable(key="'propertyValues-pid-'+ #p0.id")
    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }



}