package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import poly.edu.entity.Product;
import poly.edu.service.ProductService;

@Controller
public class ProductDetailController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product/detail/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/home";
        }
        model.addAttribute("product", product);
        return "product/detail";
    }

    @GetMapping("/product/detail")
    public String showDetailStaticPage() {
        return "product/detail"; 
    }
}

