package com.person.tm.interceptor;

import com.person.tm.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//登录拦截器
public class LoginInterceptor implements HandlerInterceptor {


    //    preHandle：预处理回调方法，实现处理器的预处理（如登录检查），第三个参数为响应的处理器
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        //contextPath
        String contextPath = httpServletRequest.getServletContext().getContextPath();
        //  System.out.println("--------contentPath:  " + contextPath);
        //  --------contentPath:  /tmall_springboot

        //需要验证登录的请求
        String[] requireAuthPages = new String[]{
                "buy",
                "alipay",
                "payed",
                "cart",
                "bought",
                "confirmPay",
                "orderConfirmed",

                "forebuyone",
                "forebuy",
                "foreaddCart",
                "forecart",
                "forechangeOrderItem",
                "foredeleteOrderItem",
                "forecreateOrder",
                "forepayed",
                "forebought",
                "foreconfirmPay",
                "foreorderConfirmed",
                "foredeleteOrder",
                "forereview",
                "foredoreview"

        };

        //uri
        String uri = httpServletRequest.getRequestURI();
        //System.out.println("--------uri:    " + uri);
        //  --------uri:    /tmall_springboot/cart

        //通过 remove方法截取 uri 一部分。
        String remove = StringUtils.remove(uri, contextPath + "/");
        //  System.out.println("---------remove: " + remove);
        //---------remove: cart

 /* 这里使用的是 普通的判断是否登录方式
        //验证登录
        if (beginWith(remove, requireAuthPages)) {
            //当进来时，表明该请求路径是需要登录的。
            User user = (User) httpServletRequest.getSession().getAttribute("user");
            //判断是否已经 登录
            if (user == null) {
                //没有登录，则跳转登录页面.
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
 */


        //这是使用shiro 进行验证 是否登录
        //验证登录
        if (beginWith(remove, requireAuthPages)) {
            //SecurityUtils 是shiro 的方式
            Subject subject = SecurityUtils.getSubject();
            // subject.isAuthenticated() 为true 时，表示已经登录。为false时，表示没有登录
            if (!subject.isAuthenticated()) {
                //没有登录，则跳转登录页面.
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
        return true;

    }

    //判断请求的路径，是否是需要验证登录。path 表示请求的资源地址，strs 表示需要判断是否需要登录。
    private boolean beginWith(String path, String[] strs) {
        //判断是否需要登录的标志
        boolean flag = false;
        for (String str : strs) {
            if (StringUtils.startsWith(path, str)) {
                //表示，该path请求资源路径 是需要登录的
                flag = true;
                break;
            }
        }
        return flag;

    }


    //postHandle：后处理回调方法，实现处理器的后处理（但在渲染视图之前）
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /*
    afterCompletion：整个请求处理完毕回调方法，即在视图渲染完毕时回调，
    如性能监控中我们可以在此记录结束时间并输出消耗时间，
    还可以进行一些资源清理，类似于try-catch-finally中的finally
   */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
