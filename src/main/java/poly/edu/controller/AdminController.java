package poly.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@GetMapping("/crud_account")
	public String crud_account(Model model) {
		model.addAttribute("view", "admin/crud_account");
		return "admin/dashboard";
	}
	
	@GetMapping("/crud_product")
	public String crud_product(Model model) {
		model.addAttribute("view", "admin/crud_product");
		return "admin/dashboard";
	}
}
