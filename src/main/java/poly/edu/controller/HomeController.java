package poly.edu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import poly.edu.dao.ProductDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Category;
import poly.edu.entity.Product;
import poly.edu.service.CartService;
import poly.edu.service.ProductService;

@Controller
public class HomeController {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductDAO productDAO;
	@Autowired
	private CartService cartService;

	@RequestMapping({ "/", "/home" })
	public String filterByCategory(Model model, @RequestParam(value = "categoryId", required = false) Long categoryId,
			HttpSession session) {
		List<Product> products;

		if (categoryId == null) {
			products = productService.findAll();
		} else {
			products = productService.findByCategoryId(categoryId);
		}
		List<Category> categories = productService.findAllCategories();

		model.addAttribute("products", products);
		model.addAttribute("categories", categories);

			return "index_layout";
	}
}
