package poly.edu.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import poly.edu.entity.Product;
import poly.edu.service.ProductService;
import poly.edu.service.CategoryService;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String showProduct(@RequestParam(value = "message", required = false) String message,
                              Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll()); // 🟢 THÊM DÒNG NÀY
        model.addAttribute("view", "admin/product_crud");

        if (message != null) {
            model.addAttribute("message", message);
        }
        return "admin/product_crud";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Product pro = productService.findById(id);
        if (pro != null) {
            model.addAttribute("product", pro);
            model.addAttribute("message", "Đang chỉnh sửa sản phẩm: " + pro.getProductName());
        } else {
            model.addAttribute("product", new Product());
            model.addAttribute("message", "Không tìm thấy sản phẩm!");
        }
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll()); // thêm dòng này
        return "admin/product_crud";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam("imageFile") MultipartFile file,
                       Model model,
                       RedirectAttributes ra) {
        try {
            // Xử lý ảnh
            if (!file.isEmpty()) {
                String fileName = Path.of(file.getOriginalFilename()).getFileName().toString();
                String uploadDir = new File("src/main/resources/static/image/img_sanpham").getAbsolutePath();
                File saveFolder = new File(uploadDir);
                if (!saveFolder.exists()) saveFolder.mkdirs();
                Path savePath = Paths.get(uploadDir, fileName);
                Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImages(fileName);
            } else {
                if (product.getProductID() != null) {
                    Product existing = productService.findById(product.getProductID());
                    if (existing != null) {
                        product.setImages(existing.getImages());
                    }
                } else {
                    model.addAttribute("message", "Vui lòng chọn ảnh sản phẩm!");
                    model.addAttribute("product", product);
                    model.addAttribute("products", productService.findAll());
                    model.addAttribute("categories", categoryService.findAll());
                    return "admin/product_crud";
                }
            }

            // Lưu sản phẩm
            productService.save(product);
            ra.addFlashAttribute("message", "Lưu sản phẩm thành công ✅");
            ra.addFlashAttribute("alertType", "success");
            return "redirect:/admin/products";

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "Lỗi khi lưu ảnh!");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Lỗi hệ thống: " + e.getMessage());
        }

        model.addAttribute("product", product);
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "admin/product_crud";
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Product deleted = productService.findById(id);
        if (deleted != null) {
            productService.deleteById(id);
            ra.addFlashAttribute("message", "🗑️ Đã xóa sản phẩm: " + deleted.getProductName());
            ra.addFlashAttribute("alertType", "danger");
        } else {
            ra.addFlashAttribute("message", "⚠️ Không tìm thấy sản phẩm để xóa!");
            ra.addFlashAttribute("alertType", "warning");
        }
        return "redirect:/admin/products";
    }
    
    @RequestMapping("/product")
    public class ProductViewController {

        @Autowired
        private ProductService productService;
    }
    @GetMapping("/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/error/404"; // hoặc trang báo lỗi
        }
        model.addAttribute("product", product);
        return "product/detail"; // Trỏ đến file: templates/product/detail.html
    }
}
