package com.person.tm.service;

import com.person.tm.dao.CategoryDAO;
import com.person.tm.pojo.Category;

import com.person.tm.pojo.Order;
import com.person.tm.pojo.Product;
import com.person.tm.pojo.User;
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
@CacheConfig(cacheNames="categories")
public class CategoryService {
    @Autowired CategoryDAO categoryDAO;

    @CacheEvict(allEntries=true)
//	@CachePut(key="'category-one-'+ #p0")
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
//	@CacheEvict(key="'category-one-'+ #p0")
    public void delete(int id) {
        categoryDAO.deleteById(id);
    }


    @Cacheable(key="'categories-one-'+ #p0")
    public Category get(int id) {
        Category c= categoryDAO.getOne(id);
        return c;
    }

    @CacheEvict(allEntries=true)
//	@CachePut(key="'category-one-'+ #p0")
    public void update(Category bean) {
        categoryDAO.save(bean);
    }


    //带分页的 list
    //这个和获取一个，其实没什么区别，就是key不一样，数据不再是一个对象，而是一个集合。 （保存在 redis 里是一个 json 数组）
    //如图所示 categories-page-0-5 就是第一页数据
    @Cacheable(key="'categories-page-'+#p0+ '-' + #p1")
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size,sort);
        Page pageFromJPA =categoryDAO.findAll(pageable);

        //	RestPage<Category> restPage = new RestPage<>(pageFromJPA);

        return new Page4Navigator<Category>(pageFromJPA,navigatePages);
    }

    @Cacheable(key="'categories-all'")
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    //这个方法的用处是删除Product对象上的 分类。 为什么要删除呢？ 因为在对分类做序列还转换为 json 的时候，会遍历里面的 products, 然后遍历出来的产品上，又会有分类，接着就开始子子孙孙无穷溃矣地遍历了，就搞死个人了
    //而在这里去掉，就没事了。 只要在前端业务上，没有通过产品获取分类的业务，去掉也没有关系

    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products =category.getProducts();
        if(null!=products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }

        List<List<Product>> productsByRow =category.getProductsByRow();
        if(null!=productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p: ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}
