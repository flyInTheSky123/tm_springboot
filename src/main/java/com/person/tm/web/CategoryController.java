package com.person.tm.web;

import com.person.tm.pojo.Category;
import com.person.tm.service.CategoryService;
import com.person.tm.util.ImageUtil;
import com.person.tm.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// @Controller + @ResponseBody  === @RestController
//分类控制器。
/*注意：上传的 params(参数)的位置，在url的什么地方。1 /{id}  2,"?param="+xxx  3,直接通过 axios.post/put(url ,params)上传
        1.1， var url=/url/{id} 这类的格式使用 @PathVariable("cid") int cid 获取数据
        2.1， var url=/url?params=y 这类的格式使用   @RequestParam(value = "params", defaultValue = "0") int params。
        3.1， 在html 中，如果使用   axios.put(url,params) 。
          （1）其中bean 是定义过的并且没有使用 FormData，在 controller 中使用（ @RequestBody POJO（类型） bean ）获取数据
          （2）其中bean中使用 FormData  格式，在  controller  中使用(Category bean, MultipartFile image, HttpServletRequest request)获取数据。
       因为：  FormData 格式是用于图片/文件（MultipartFile image ） 上传 。具体使用方式 请看 listCategory.html 的图片上传。
*/
@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    //查询所有
    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Category> page = categoryService.list(start, size, 5);  //5表示导航分页最多有5个，像 [1,2,3,4,5] 这样
        return page;
    }

    //添加分类
    @PostMapping("/categories")
    public Object addCatgory(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {

        //1 ,保存分类的名称。
        categoryService.add(bean);

        //调用 保存图片方法
        saveOrUpdateImage(bean, image, request);
        return bean;

    }

    //保存图片方法.
    public void saveOrUpdateImage(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        //获取路径
        String realPath = request.getServletContext().getRealPath("img/category");
        //创建文件
        File imageFolder = new File(realPath);

        //创建jpg 文件
        File imageFile = new File(imageFolder, bean.getId() + ".jpg");

        //判断是否存在
        if (!imageFile.getParentFile().exists()) {
            //文件不存在，则创建
            imageFile.getParentFile().mkdirs();
        }
        //将io 写进文件
        image.transferTo(imageFile);
        //确保文件是jpg
        BufferedImage bufferedImage = ImageUtil.change2jpg(imageFile);
        //写入
        ImageIO.write(bufferedImage, "jpg", imageFile);


    }


    //删除分类数据。
    @DeleteMapping("/categories/{id}")
    public String deleteCategory(@PathVariable("id") int id, HttpServletRequest request) {
        //删除数据库信息
        categoryService.delete(id);
        //获取路径
        File imageFolder = new File(request.getServletContext().getRealPath("/img/category"));
        File file = new File(imageFolder, id + ".jpg");
        //删除图片
        file.delete();
        return null;
    }

    //通过id 获取
    @GetMapping("/categories/{id}")
    public Category findOne(@PathVariable("id") int id) {
        Category one = categoryService.get(id);
        return one;
    }

    @PutMapping("/categories/{id}")
    public void update(Category bean, MultipartFile image, HttpServletRequest request) throws IOException {
        //   @Put，要自己获取参数，Mapping 中
        String name = request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);
        //System.out.println("--------+------:" + bean.getName());
        if (image!=null) {
            saveOrUpdateImage(bean, image, request);
        }


    }


}
