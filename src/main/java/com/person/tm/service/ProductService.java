package com.person.tm.service;

import com.person.tm.dao.ProductDAO;
import com.person.tm.es.ProductESDAO;
import com.person.tm.pojo.Category;
import com.person.tm.pojo.OrderItem;
import com.person.tm.pojo.Product;
import com.person.tm.util.Page4Navigator;
import com.person.tm.util.SpringContextUtil;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@CacheConfig(cacheNames="products")
public class ProductService  {

    @Autowired ProductDAO productDAO;
    @Autowired ProductImageService productImageService;
    @Autowired CategoryService categoryService;
    @Autowired OrderItemService orderItemService;
    @Autowired ReviewService reviewService;

    @Autowired
    ProductESDAO productESDAO;
    @CacheEvict(allEntries=true)
    public void add(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @CacheEvict(allEntries=true)
    public void delete(int id) {
        Product product = productDAO.getOne(id);
        productDAO.delete(product);
        productESDAO.delete(product);

    }

    @Cacheable(key="'products-one-'+ #p0")
    public Product get(int id) {
        Optional<Product> byId = productDAO.findById(id);
        return byId.get();
    }

    @CacheEvict(allEntries=true)
    public void update(Product bean) {
        productDAO.save(bean);
        productESDAO.save(bean);
    }

    @Cacheable(key="'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size,int navigatePages) {
        Category category = categoryService.get(cid);
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);




        Page<Product> pageFromJPA =productDAO.findByCategory(category,pageable);
        //RestPage<Product> products = new RestPage<>(pageFromJPA);

        return new Page4Navigator<>(pageFromJPA,navigatePages);
        // pageFromJPA,navigatePages
    }

    //————————————————————————————前端---------------
    public void fill(List<Category> categorys) {
        for (Category category : categorys) {
            fill(category);
        }
    }


    @Cacheable(key="'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category){
        return productDAO.findByCategoryOrderById(category);
    }
    // 这个 listByCategory 方法本来就是 ProductService 的方法，却不能直接调用。
    // 为什么呢？因为 springboot 的缓存机制是通过切面编程 aop来实现的。
    // 从fill方法里直接调用 listByCategory 方法， aop 是拦截不到的，也就不会走缓存了。
    // 所以要通过这种 绕一绕 的方式故意诱发 aop, 这样才会想我们期望的那样走redis缓存。

    public void fill(Category category) {
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);
        productImageService.setFirstProdutImages(products);
        category.setProducts(products);
    }


    public void fillByRow(List<Category> categorys) {
        int productNumberEachRow = 8;
        for (Category category : categorys) {
            List<Product> products =  category.getProducts();
            List<List<Product>> productsByRow =  new ArrayList<>();
            for (int i = 0; i < products.size(); i+=productNumberEachRow) {
                int size = i+productNumberEachRow;
                size= size>products.size()?products.size():size;
                List<Product> productsOfEachRow =products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }


    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);


        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);

    }


    public void setSaleAndReviewNumber(List<Product> products) {
        for (Product product : products)
            setSaleAndReviewNumber(product);
    }

//	public List<Product> search(String keyword, int start, int size) {
//		Sort sort = new Sort(Sort.Direction.DESC, "id");
//		Pageable pageable = new PageRequest(start, size, sort);
//		List<Product> products =productDAO.findByNameLike("%"+keyword+"%",pageable);
//		return products;
//	}

    public List<Product> search(String keyword, int start, int size) {
        initDatabase2ES();

        String searchs = "";
        // 构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本分词查询
        queryBuilder.withQuery(QueryBuilders.matchQuery("name", keyword));
        // 搜索，获取结果
        Page<Product> items = productESDAO.search(queryBuilder.build());
        // 总条数
        //long total = items.getTotalElements();

        //	System.out.println("total: "+total);

        return items.getContent();
    }

    private void initDatabase2ES() {
        Pageable pageable = new PageRequest(0, 5);
        Page<Product> page =productESDAO.findAll(pageable);
        if(page.getContent().isEmpty()) {
            List<Product> products= productDAO.findAll();
            //System.out.println(" ---initDatabase2ES: null ----");
            for (Product product : products) {
                System.out.println("product :"+product);
                productESDAO.save(product);
            }
        }
    }


}


