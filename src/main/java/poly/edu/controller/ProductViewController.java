package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import poly.edu.entity.Product;
import poly.edu.service.ProductService;

@Controller
@RequestMapping("/product")
public class ProductViewController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public String productDetail(@PathVariable("id") Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/error/404"; // Hoặc bạn có thể hiển thị thông báo lỗi
        }
        model.addAttribute("product", product);
        return "product/detail"; // file: templates/product/detail.html
    }
}
