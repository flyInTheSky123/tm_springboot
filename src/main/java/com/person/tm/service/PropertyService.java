package com.person.tm.service;

import com.person.tm.dao.CategoryDAO;
import com.person.tm.dao.PropertyDAO;
import com.person.tm.pojo.Category;
import com.person.tm.pojo.Property;
import com.person.tm.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames="properties")
public class PropertyService {

    @Autowired PropertyDAO propertyDAO;
    @Autowired CategoryService categoryService;

    @CacheEvict(allEntries=true)
    public void add(Property bean) {
        propertyDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id) {

        propertyDAO.deleteById(id);
    }

    @Cacheable(key="'properties-one-'+ #p0")
    public Property get(int id) {
        return propertyDAO.getOne(id);
    }

    @CacheEvict(allEntries=true)
    public void update(Property bean) {
        propertyDAO.save(bean);
    }
    @Cacheable(key="'properties-cid-'+ #p0.id")
    public List<Property> listByCategory(Category category){
        return propertyDAO.findByCategory(category);
    }


    @Cacheable(key="'properties-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Property> list(int cid, int start, int size,int navigatePages) {
        Category category = categoryService.get(cid);

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);

        Page<Property> pageFromJPA =propertyDAO.findByCategory(category,pageable);

        //	RestPage<Property> Propertys = new RestPage<>(pageFromJPA);
        return new Page4Navigator<>(pageFromJPA,navigatePages);


    }

}
