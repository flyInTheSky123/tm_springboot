package com.person.tm.web;

import com.person.tm.dao.PropertyValueDAO;
import com.person.tm.pojo.Property;
import com.person.tm.pojo.PropertyValue;
import com.person.tm.service.PropertyService;
import com.person.tm.service.PropertyValueService;
import com.person.tm.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.GeneratedValue;
import java.util.List;

//属性控制器：
/*注意：上传的参数的位置，在url的什么地方。1 /{id}  2,?param=xxx  3,直接通过 axios.post/put(url ,params)上传
        1.1， /xxxxxs/{id} 这类的格式使用 @PathVariable("cid") int cid 获取数据
        2.1，/xxxxxxs ? start=y 这类的格式使用   @RequestParam(value = "start", defaultValue = "0") int start。
        3.1，在html 中，如果使用   axios.put(url,vue.bean) 。
          （1）其中bean 是定义过的并且没有使用 FormData，在controller中使用（ @RequestBody POJO（类型） bean ）获取数据
          （2）其中bean中使用 FormData  格式，在controller中使用(Category bean, MultipartFile image, HttpServletRequest request)获取数据。
       因为：  FormData 格式是用于图片/文件（MultipartFile image ） 上传 。具体使用方式 请看 listCategory.html 的图片上传。
*/
@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    @Autowired
    PropertyValueDAO propertyValueDAO;


    //查询所有分类数据
    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Property> page = propertyService.list(cid, start, size, 5);
        return page;
    }

    //这里可以通过 id 获取property ，category（因为在POJO 中 property添加了 category 属性。）。
    @GetMapping("/properties/{id}")
    public Property get(@PathVariable("id") int id) {
        Property property = propertyService.get(id);
        return property;
    }

    //添加属性。
    @PostMapping("/properties")
    public Object add(@RequestBody Property bean) throws Exception {
        propertyService.add(bean);
        //System.out.println(" bean :" + bean.getName() + "  " + bean.getCategory().getId());
        return bean;
    }

    //更新数据
    @PutMapping("/properties")
    public Object update(@RequestBody Property property) {
        propertyService.update(property);
        return property;
    }

    //删除分类属性时，一定要先把该分类下的所有的产品的属性对应的的属性值先删除。
    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") int id) {
        //通过 property 的ID 获取信息
        // Property property = propertyService.get(id);

        //通过 PropertyValue的property 删除
        // propertyValueDAO.deleteByProperty(property);
        //  System.out.println("----------pv:  "+pv.getProduct().getName());
        //2，再删除属性
        propertyService.delete(id);
        return null;

    }


}
