package com.person.tm.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//用来进行后台的页面跳转
@Controller
public class AdminPageController {

    //重定向到admin_category_list
    @GetMapping(value = "/admin")
    public String admin(){
        return "redirect:admin_category_list";

    }

    //跳到页面 listCategory.html
    @GetMapping(value = "/admin_category_list")
    public String listCategory(){
        return "admin/listCategory";
    }


    //跳到页面 editCategory.html
    @GetMapping(value = "/admin_category_edit")
    public String editCategory(){
        return "admin/editCategory";
    }


    //跳转页面listProperty.html
    @GetMapping(value = "/admin_property_list")
    public String listProperty(){
        return "admin/listProperty";
    }

    //跳转页面editProperty.html
    @GetMapping(value = "/admin_property_edit")
    public String editProperty(){
        return "admin/editProperty";
    }


    @GetMapping(value="/admin_order_list")
    public String listOrder(){
        return "admin/listOrder";

    }

    @GetMapping(value="/admin_product_list")
    public String listProduct(){
        return "admin/listProduct";

    }

    @GetMapping(value="/admin_product_edit")
    public String editProduct(){
        return "admin/editProduct";

    }
    @GetMapping(value="/admin_productImage_list")
    public String listProductImage(){
        return "admin/listProductImage";

    }


    @GetMapping(value="/admin_propertyValue_edit")
    public String editPropertyValue(){
        return "admin/editPropertyValue";

    }

    @GetMapping(value="/admin_user_list")
    public String listUser(){
        return "admin/listUser";

    }
}
