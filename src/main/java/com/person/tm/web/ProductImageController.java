package com.person.tm.web;

import com.person.tm.dao.ProductImageDao;
import com.person.tm.pojo.Product;
import com.person.tm.pojo.ProductImage;
import com.person.tm.service.CategoryService;
import com.person.tm.service.ProductImageService;
import com.person.tm.service.ProductService;
import com.person.tm.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*注意：上传的参数的位置，在url的什么地方。1 /{id}  2,?param=xxx  3,直接通过 axios.post/put(url ,params)上传
        1.1， /xxxxxs/{id} 这类的格式使用 @PathVariable("cid") int cid 获取数据
        2.1，/xxxxxxs ? start=y 这类的格式使用   @RequestParam(value = "start", defaultValue = "0") int start。
        3.1，在html 中，如果使用   axios.put(url,vue.bean) 。
          （1）其中bean 是定义过的并且没有使用 FormData，在controller中使用（ @RequestBody POJO（类型） bean ）获取数据
          （2）其中bean中使用 FormData  格式，在controller中使用(Category bean, MultipartFile image, HttpServletRequest request)获取数据。
       因为：  FormData 格式是用于图片/文件（MultipartFile image ） 上传 。具体使用方式 请看 listCategory.html 的图片上传。
*/
@RestController
public class ProductImageController {

    @Autowired
    ProductImageService productImageService;
    @Autowired
    ProductService productService;
    @Autowired
    CategoryService categoryService;


    //根据产品ID 获取图片。
    @GetMapping("/products/{pid}/productImages")
    public List<ProductImage> list(@PathVariable("pid") int pid, @RequestParam("type") String type) {
        //通过id 获取 相应的产品信息。
        Product product = productService.get(pid);

        //判断当前是请求哪一类型图片
        if (productImageService.type_single.equals(type)) {
            //请求单个图片类型
            List<ProductImage> productImages = productImageService.listSingleProductImages(product);
            return productImages;

        } else if (productImageService.type_detail.equals(type)) {
            //请求详情图片
            List<ProductImage> productImages = productImageService.listDetailProductImages(product);
            return productImages;

        } else {
            return new ArrayList<>();
        }

    }

    //添加图片，1，将信息存进数据库 2，将图片写进文件路径里面
    @PostMapping("/productImages")
    public Object add(@RequestParam("pid") int pid, @RequestParam("type") String type, MultipartFile file, HttpServletRequest request) {

        //1，根据id 获取产品信息
        Product product = productService.get(pid);

        //设置productImage 信息
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setType(type);
        //增加产品图片信息
        productImageService.add(productImage);


        //文件路径位置
        String folder = "img/";
        //判断当前图片的类型，确定图片存储位置
        if (productImageService.type_single.equals(productImage.getType())) {
            //如果是 单个图片,则路径是productSingle
            folder += "productSingle";
        } else {
            folder += "productDetail";
        }

        File path = new File(request.getServletContext().getRealPath(folder));
        File imgFile = new File(path, productImage.getId() + ".jpg");

        String imgName = imgFile.getName();

        //判断当前路径是否存在
        if (!imgFile.getParentFile().exists()) {
            imgFile.getParentFile().mkdirs();
        }

        // 2，将图片写进路径里面
        try {
            file.transferTo(imgFile);
            BufferedImage bufferedImage = ImageUtil.change2jpg(imgFile);
            ImageIO.write(bufferedImage, "jpg", imgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将single 图片的格式大小改为 small,middle;
        if (productImageService.type_single.equals(productImage.getType())) {

            String middleFolder = request.getServletContext().getRealPath("img/productSingle_middle");
            String smallFloder = request.getServletContext().getRealPath("img/productSingle_small");

            //middle型图片文件
            File middleFile = new File(middleFolder, imgName);
            //small型图片文件
            File smallFile = new File(smallFloder, imgName);

            //判断是否存在
            if (!middleFile.getParentFile().exists()) {
                middleFile.mkdirs();
            }
            if (!smallFile.getParentFile().exists()) {
                smallFile.mkdirs();
            }

            ImageUtil.resizeImage(imgFile, 56, 56, smallFile);
            ImageUtil.resizeImage(imgFile, 217, 190, middleFile);
        }

        return productImage;

    }


    //删除图片
    @DeleteMapping("/productImages/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) {
        //通过id 获取productImage信息
        ProductImage productImage = productImageService.get(id);
        //删除数据库图片信息。
        productImageService.delete(id);

        //进行删除图片文件准备
        //图片路径
        String folder = "img/";
        //判断要删除的图片的类型
        if (productImageService.type_single.equals(productImage.getType())) {
            //要删除图片的类型是 single
            folder += "productSingle";

        } else if (productImageService.type_detail.equals(productImage.getType())) {
            //要删除图片的类型是 detail
            folder += "productDetail";
        }

        //创建文件
        File path = new File(request.getServletContext().getRealPath(folder));
        //该图片
        File imgFile = new File(path, productImage.getId() + ".jpg");
        //图片名称
        String fileName = imgFile.getName();

        //删除图片,如果是single 型的图片，还要删除middle,small 型的图片
        imgFile.delete();

        //删除productSingle_middle ,productSingle_small 型图片
        if (productImageService.type_single.equals(productImage.getType())) {
            //文件夹
            String middleFile = request.getServletContext().getRealPath("img/productSingle_middle");
            String smallFile = request.getServletContext().getRealPath("img/productSingle_small");

            //图片
            File smallImg = new File(smallFile, fileName);
            File middleImg = new File(middleFile, fileName);

            //删除图片
            smallImg.delete();
            middleImg.delete();


        }
        return null;


    }


}
