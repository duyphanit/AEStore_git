package poly.edu.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import poly.edu.dao.AccountDAO;
import poly.edu.entity.Account;
import poly.edu.service.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountDAO accountDAO;
	
	@Autowired
	private AccountService accountService;

	@GetMapping("/login")
	public String LoginForm() {
		return "/account/login";
	}

	@PostMapping("/login")
	public String doLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			HttpSession session, Model model) {
		Account account = accountDAO.findByEmail(email).stream().findFirst().orElse(null);
		if (account != null && account.getPassword().equals(password)) {
			session.setAttribute("email", account.getEmail());
			session.setAttribute("role", account.isRole() ? "ADMIN" : "USER");
			
			// getFullname
			session.setAttribute("fullname", account.getFullname());
			session.setAttribute("message", "Đăng nhập thành công!");
			return "redirect:/home";
		}
		model.addAttribute("error", "Email hoặc mật khẩu chưa đúng!");
		return "/account/login";
	}

	@GetMapping("/register")
	public String RegisterForm(Model model) {
		model.addAttribute("account", new Account());
		
		return "/account/register";
	}

	@PostMapping("/register")
	public String doRegister(@Valid @ModelAttribute("account") Account account, BindingResult result, Model model,
			HttpSession session) {
		if (result.hasErrors()) {
			return "/account/register";
		}

		// Kiểm tra đuôi email phải là @gmail.com
		if (!account.getEmail().toLowerCase().endsWith("@gmail.com")) {
			model.addAttribute("message", "Email không đúng định dạng @gmail.com");
			return "/account/register";
		}

		// Kiểm tra email đã tồn tại
		if (accountDAO.existsByEmail(account.getEmail())) {
			model.addAttribute("message", "Email đã tồn tại");
			return "/account/register";
		}
		
		String password = account.getPassword();
		if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")) {
			model.addAttribute("passEr", "Mật khẩu phải có ít nhất 6 ký tự bao gồm cả chữ và số");
		    return "/account/register";
		}

		account.setRole(false);
		accountDAO.save(account);

		// Lưu thông tin vào session
		session.setAttribute("email", account.getEmail());
		session.setAttribute("role", account.isRole() ? "ADMIN" : "USER");

		return "redirect:/home";
	}

	// Logout
	@GetMapping("/account/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/account/login";
	}

//		@GetMapping("/cart")
//		public String showCartPage() {
//		     return "shopping/cart"; // đúng đường dẫn tới file cart.html
//		}
	
//	Login with google
	@GetMapping({ "/", "/home" })
	public String login_GG(Model model, OAuth2AuthenticationToken auth, HttpSession session) {
		OAuth2User user = auth.getPrincipal();
		String name = user.getAttribute("name");
		String email = user.getAttribute("email");
		
		// Lưu vào db
		Optional<Account> account = accountDAO.findByEmail(email);
		Account acc = null;
		
		if (account.isPresent()) {
			acc = account.get(); //account đã tồn tại
		} else {
			// tạo mới
			account = Optional.of(new Account());
			acc.setEmail(email);
			acc.setFullname(name);
			acc.setPassword("abc123");
			acc.setPhone("");
			acc.setAddress("");
			acc.setRole(false);
			
			accountDAO.save(acc);
		}
		
		//Lưu vào session
		session.setAttribute("fullname", acc.getFullname());
		session.setAttribute("email", acc.getEmail());
		session.setAttribute("phone", acc.getPhone());
	    session.setAttribute("address", acc.getAddress());
	    session.setAttribute("role", acc.isRole()); 
		
		model.addAttribute("name", name);
		return "index_layout";
	}
	
	// Profile
		@GetMapping("/profile")
		public String showProfile(Model model, HttpSession session) {
			String email = (String) session.getAttribute("email");
			if (email == null) {
				return "redirect:/account/login";
			}

			Account account = accountService.getByEmail(email);
			model.addAttribute("account", account);
			return "account/profile";
		}
		
		@PostMapping("/profile/update")
		public String updateProfile(@ModelAttribute("account") Account formAccount, HttpSession session,
				RedirectAttributes ra) {
			String email = (String) session.getAttribute("email");
			Account acc = accountService.getByEmail(email);
			
			//Update
			acc.setEmail(formAccount.getEmail());
			acc.setFullname(formAccount.getFullname());
			acc.setPhone(formAccount.getPhone());
			acc.setAddress(formAccount.getAddress());
			
			accountService.save(acc);
			ra.addFlashAttribute("message", "Cập nhật thành công!");
			return "redirect:/account/profile";
		}
}
