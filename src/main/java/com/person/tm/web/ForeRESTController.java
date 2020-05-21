package com.person.tm.web;

import com.person.tm.comparator.*;
import com.person.tm.pojo.*;
import com.person.tm.service.*;
import com.person.tm.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    @Autowired
    OrderService orderService;

    @Autowired
    PropertyValueService propertyValueService;


    @GetMapping("/forehome")
    public Object home() {
        List<Category> list = categoryService.list();

        //为每个分类进行填充product，image
        productService.fill(list);
        productService.fillByRow(list);

        //取消product中的category属性。
        categoryService.removeCategoryFromProduct(list);

        return list;

    }

    //注册用户
    //后期加入shiro 。把密码进行加密
    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);

        //判断该用户名是否已经存在
        boolean exist = userService.isExist(name);

        if (exist) {
            //该用户名已经存在。
            String message = "该用户名已经存在";
            //返回注册失败code。
            return Result.fail(message);

        }


        //盐，用于进行加密
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        //加密次数
        int times = 2;

        //计算法则 名称 md5
        String algorithmName = "md5";

        //加密
        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();

        //将加密后的密码进行放入数据库中
        user.setPassword(encodedPassword);
        user.setSalt(salt);
        //不存在重复的用户名时，直接注册
        userService.add(user);

        //返回注册成功的code
        return Result.success();


    }


    //登录。使用shiro进行校验
    @PostMapping("forelogin")
    public Object login(@RequestBody User user, HttpSession session) {
        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        //User login = userService.getByNameAndPassowrd(name, password);

        //获取subject
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, password);

        try {
            //登录，如果账号密码与数据中不匹配则报错。
            subject.login(token);
            //没有报错，则说明 登录成功
            User byName = userService.getByName(name);
            session.setAttribute("user", byName);
            return Result.success();


        } catch (AuthenticationException e) {
            String message = "账号密码错误";
            return Result.fail(message);
        }

//
//
//        if (login == null) {
//            //该user不存在
//            String message = "该用户不存在";
//            return Result.fail(message);
//        } else {
//            //存在，登录成功
//            session.setAttribute("user", login);
//            return Result.success();
//
//        }

    }


    //判断是否已经登录。使用shiro的方式判断。
    @GetMapping("forecheckLogin")
    public Object isLogin(HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        if (user != null) {
//            return Result.success();
//        }
//        return Result.fail("请先登录！");

        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated())
            return Result.success();
        else
            return Result.fail("未登录");
    }

    //前端页面产品详情
    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        //通过id获取product
        Product product = productService.get(pid);

        List<ProductImage> detailImages = productImageService.listDetailProductImages(product);

        List<ProductImage> singleImages = productImageService.listSingleProductImages(product);
        productImageService.setFirstProdutImage(product);

        //为产品设置single/detail 图片
        product.setProductDetailImages(detailImages);
        product.setProductSingleImages(singleImages);

        //为产品设置 销售量，评价量
        int saleCount = orderItemService.getSaleCount(product);
        int reviewCount = reviewService.getCount(product);

        product.setSaleCount(saleCount);
        product.setReviewCount(reviewCount);

        //该产品的属性
        List<PropertyValue> pvs = propertyValueService.list(product);

        //该产品的评价
        List<Review> list = reviewService.list(product);

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", list);

        //为什么要用Map呢？ 因为返回出去的数据是多个集合，而非一个集合，所以通过 map返回给浏览器，浏览器更容易识别
        return Result.success(map);

    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid, String sort) {
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        List<Product> products = category.getProducts();
        if (null != sort) {
            switch (sort) {
                case "review":

                    category.getProducts().sort(Comparator.comparing(Product::getReviewCount));
                  //  Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    category.getProducts().sort(Comparator.comparing(Product::getCreateDate).reversed());
                    //Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;

                case "saleCount":
                    category.getProducts().sort(Comparator.comparing(Product::getSaleCount).reversed());
                    //Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;

                case "price":
                    category.getProducts().sort(Comparator.comparing(Product::getPromotePrice));
                //    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;

                case "all":
                    category.getProducts().sort(Comparator.comparing(Product::getSaleCount));
                //    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }

        return category;
    }

    //搜索
    @PostMapping("foresearch")
    public Object search(String keyword) {
        if (null == keyword)
            keyword = "";
        List<Product> ps = productService.search(keyword, 0, 20);
        //设置该产品的图片
        productImageService.setFirstProdutImages(ps);
        //设置该产品的销售与评价数量
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }


    //直接购买,因为 在进行 前端"添加购物车" 操作时，都会使用该方法进行判断产品在 订单项中是否存在。

    @GetMapping("forebuyone")
    public Object buyOne(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid, num, session);
    }

    /**
     * 在将产品进行添加到购物车时，首先：
     * 1，判断该产品 是否已经存在于 该用户的购物车中
     * 2，如果，存在，则修改该产品订单项的数量
     * 3，如果不存，则添加订单项。
     * 4，返回订单项 id。
     **/

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        User user = (User) session.getAttribute("user");
        //获取该产品的信息
        Product product = productService.get(pid);

        //订单项id oiid
        int oiid = 0;
        boolean find = false;

        List<OrderItem> orderItems = orderItemService.listByUser(user);
        //遍历，判断
        for (OrderItem oi : orderItems) {
            if (oi.getProduct().getId() == product.getId()) {
                //当订单项中，已经存在该产品时，修改数量,更新订单项
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                //修改已经查到的标志
                find = true;
                oiid = oi.getId();
                break;

            }
        }

        if (!find) {
            //没有查找到时,重新创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setNumber(num);
            orderItem.setProduct(product);
            orderItem.setUser(user);
            orderItemService.add(orderItem);

            oiid = orderItem.getId();

        }
        //System.out.println("oiid: " + oiid);
        return oiid;
    }




    //订单项结算页面(包含来自于 "直接购买功能" 和 "购物车结算功能")
    @GetMapping("forebuy") //这里String[] ooid 之所以 是数组，是因为在购物车里可以有多个订单项。
    public Object buy(String[] oiid, HttpSession session) {

        //总付款金额
        float total = 0;

        //获取的订单项
        List<OrderItem> orderItems = new ArrayList<>();
        //遍历订单项id
        for (String strid : oiid) {
            //转换为int
            int id = Integer.parseInt(strid);
            //获取订单项
            OrderItem orderItem = orderItemService.get(id);
            //计算金额
            total += orderItem.getProduct().getPromotePrice() * orderItem.getNumber();
            //将该订单项放入集合。
            orderItems.add(orderItem);

        }

        //为订单项中的 产品进行图片设置。
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        //设置订单项session，为后面forecreateOrder 创建订单做准备
        session.setAttribute("ois", orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);


        return Result.success(map);
    }

    //添加购物车
    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        //调用方法，进行添加购物车。
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }

    //查看购物车
    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        //获取该用户
        User user = (User) session.getAttribute("user");
        //获取该用户下的订单项
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        //为订单项中的每个产品进行设置图片
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        return orderItems;

    }

    //通过购物车页面修改订单项数量，更新订单项中的数量
    //使用shiro的方式进行判断是否已经登录
    @GetMapping("forechangeOrderItem")
    public Object forechangeOrderItem(HttpSession session, int pid, int num) {

        User user = (User) session.getAttribute("user");

        Subject subject = SecurityUtils.getSubject();
        //isAuthenticated() 为true 时，表示已经登录
        if (subject.isAuthenticated()) {
            //已经登录
            //获取该用户下面的所有订单项
            List<OrderItem> orderItems = orderItemService.listByUser(user);
            //遍历订单项
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getProduct().getId() == pid) {
                    //如果相等，该用户下的该订单项数量已经改变
                    //修改该订单项数量
                    orderItem.setNumber(num);
                    //更新数据库
                    orderItemService.update(orderItem);
                    break;
                }
            }
        } else {
            return Result.fail("请先登录");
        }

        //返回处理结果
        return Result.success();


    }

    //删除该订单项
    //使用shiro 的方式进行判断是否已经登录
    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");

        Subject subject = SecurityUtils.getSubject();
        //subject.isAuthenticated()：为true 表示已经 登录。为false 表示未登录
        //判断是否已经登录
        if (!subject.isAuthenticated())
            return Result.fail("请登录");

        //已经登录
        //删除订单项
        orderItemService.delete(oiid);
        return Result.success();
    }

    //获取该用户的订单项数量
    //使用shiro 的方式进行判断是否已经登录
    @GetMapping("foreGetOrderItemNum")
    public Object getOrderItemNum(HttpSession session) {
        int num = 0;
        User user = (User) session.getAttribute("user");
        //判断是否已经登录
//        if (user == null)
//            return Result.fail("请登录");

        Subject subject = SecurityUtils.getSubject();
        //subject.isAuthenticated()：为true 表示已经 登录。为false 表示未登录
        //判断是否已经登录
        if (!subject.isAuthenticated())
            return Result.fail("请登录");

        //获取数量
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            num += oi.getNumber();
        }

        //返回数量
        return Result.success(num);

    }

    //创建订单，通过参数order。获取forebuy中的session 得到orderitems(要付款的商品)。
    //使用shiro 的方式进行判断是否已经登录
    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {

        User user = (User) session.getAttribute("user");
//        if (user == null)
//            return Result.fail("请登录");

        Subject subject = SecurityUtils.getSubject();
        //subject.isAuthenticated()：为true 表示已经 登录。为false 表示未登录
        //判断是否已经登录
        if (!subject.isAuthenticated())
            return Result.fail("请登录");


        //订单号：订单创建时间+四位随机数；
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());


        //从forebuy 中获取已经设置的session。
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");

        float total = orderService.add(order, ois);


        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);
        //  System.out.println("-----------oid :"+order.getId());

        return Result.success(map);

    }

    //付款成功
    @GetMapping("forepayed")
    public Object payed(int oid) {

        Order order = orderService.getOne(oid);
        //将状态改为待发货:
        //为产品更新库存
        orderItemService.fill(order);
        List<OrderItem> orderItems = order.getOrderItems();
        orderItems.forEach(orderItem -> {

            int stock = 0;
            Product product = orderItem.getProduct();
            int number = orderItem.getNumber();

            //从库存中减去 已经被付款购买的产品数量
            stock = product.getStock() - number;

            product.setStock(stock);
            productService.update(product);

        });


        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);

        return order;
    }

    //获取该用户下的除去delete状态的订单
    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
//        if (user == null)
//            return Result.fail("请登录");

        Subject subject = SecurityUtils.getSubject();
        //subject.isAuthenticated()：为true 表示已经 登录。为false 表示未登录
        //判断是否已经登录
        if (!subject.isAuthenticated())
            return Result.fail("请登录");

        List<Order> orders = orderService.listByUserWithoutDelete(user);

        orderService.removeOrderFromOrderItem(orders);
        return orders;

    }


    //点击"确认收货" 后，再次显示该订单下的订单项
    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order order = orderService.getOne(oid);
        //为该订单 填充 订单项。
        orderItemService.fill(order);
        //获取该订单下的 各个订单项的总付款金额
        orderService.getTotalMoney(order);

        //去除orderitem 下的 order 防止出现无限循环
        orderService.removeOrderFromOrderItem(order);
        return order;
    }

    //确认收货之后的 "确认支付" ,修改该订单的状态为待评价。
    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed(int oid) {
        Order o = orderService.getOne(oid);
        //修改状态为 待评价
        o.setStatus(OrderService.waitReview);
        //设置确认 时间
        o.setConfirmDate(new Date());
        //更新
        orderService.update(o);
        return Result.success();
    }

    //在  我的订单 页面 ，进行删除订单。修改该订单的状态
    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid) {

        Order order = orderService.getOne(oid);

        //修改状态为delete
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return Result.success();

    }

    //显示商品评论
    @GetMapping("forereview")
    public Object review(int oid) {
        //根据oid 获取订单
        Order order = orderService.getOne(oid);
        //为订单进行填充 订单项
        orderItemService.fill(order);

        //防止在持久化时，产生无限循环
        orderService.removeOrderFromOrderItem(order);

        //获取该订单的第一个产品（为了简化学习，只为每个订单的第一个 产品进行评价）
        Product product = order.getOrderItems().get(0).getProduct();

        //获取该产品的所有评价
        List<Review> list = reviewService.list(product);
        //为该产品进行设置出售 和 评价数量
        productService.setSaleAndReviewNumber(product);

        Map<String, Object> map = new HashMap<>();
        map.put("o", order);
        map.put("p", product);
        map.put("reviews", list);
        return Result.success(map);
    }

    //为添加产品添加评论

    @PostMapping("foredoreview")
    public Object addReview(int oid, int pid, String content, HttpSession session) {
        //通过oid订单id ，获取该订单。修改订单状态为 结束。
        Order order = orderService.getOne(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);

        //获取用户 和 产品
        User user = (User) session.getAttribute("user");

        Product product = productService.get(pid);

        //将评论内容进行处理
        content = HtmlUtils.htmlEscape(content);


        //设置评论
        Review review = new Review();
        review.setCreateDate(new Date());
        review.setContent(content);
        review.setProduct(product);
        review.setUser(user);

        reviewService.add(review);
        return Result.success();
    }


}
