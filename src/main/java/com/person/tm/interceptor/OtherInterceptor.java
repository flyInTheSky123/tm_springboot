package com.person.tm.interceptor;

import com.person.tm.pojo.Category;
import com.person.tm.pojo.OrderItem;
import com.person.tm.pojo.User;
import com.person.tm.service.CategoryService;
import com.person.tm.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/*
  其它拦截器：
  1，获取分类，放在搜索框下面
  2，计算用户购物车里面的订单项数量
  3，首页跳转
 */
public class OtherInterceptor implements HandlerInterceptor {

    @Autowired
    CategoryService categoryService;

    @Autowired
    OrderItemService orderItemService;


    //预处理
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    //后处理
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        //1，计算订单项数量
        HttpSession session = httpServletRequest.getSession();
        //获取用户
        User user = (User) session.getAttribute("user");
        //定义购物车订单项数量
        int cartTotalItemNumber = 0;
        //判断用户是否已经登录
        if (user != null) {
            //已经登录
            List<OrderItem> orderItems = orderItemService.listByUser(user);
            //遍历
            for (OrderItem orderItem : orderItems) {
                cartTotalItemNumber += orderItem.getNumber();
            }
        }
        //  session.setAttribute("cartTotalItemNumber",cartTotalItemNumber);
        httpServletRequest.getServletContext().setAttribute("cartTotalItemNumber", cartTotalItemNumber);
        // System.out.println("-------cartTotalItemNumber: " + cartTotalItemNumber);

        //2，搜索框架下面的分类，显示
        List<Category> list = categoryService.list();
        // session.setAttribute("categories_below_search",list);
        httpServletRequest.getServletContext().setAttribute("categories_below_search", list);


        //3,首页
        String contextPath = httpServletRequest.getServletContext().getContextPath();
        //session.setAttribute("contextPath",contextPath);
        httpServletRequest.getServletContext().setAttribute("contextPath", contextPath);

    }

    //处理请求完毕后。
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
