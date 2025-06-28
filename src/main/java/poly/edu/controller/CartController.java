package poly.edu.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;

import jakarta.servlet.http.HttpSession;
import poly.edu.service.CartService;
import poly.edu.dao.AccountDAO;
import poly.edu.dao.CartDAO;
import poly.edu.dao.ProductDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Cart;
import poly.edu.entity.Product;


@Controller
@RequestMapping("/cart")
public class CartController {
	@Autowired
    private CartDAO cartDAO; 
	
    @Autowired
    private CartService cartService;

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private ProductDAO productDAO;

    @GetMapping
    public String showCart(HttpSession session, Model model, Principal principal) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/account/login";
        }

        List<Cart> cartItems = cartDAO.findByAccount_Email(email);
        model.addAttribute("cartItems", cartItems);
        return "shopping/cart";
    }
    
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            return "redirect:/account/login";
        }

        Account account = accountDAO.findByEmail(email).stream().findFirst().orElse(null);
        Product product = productDAO.findById(productId).orElse(null);

        if (account != null && product != null) {
        	Optional<Cart> existingCart = cartDAO.findByAccount_AccountIDAndProduct_ProductID(account.getAccountID(), productId);
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + 1);
                cartDAO.save(cart);
            } else {
                Cart newCart = new Cart();
                newCart.setAccount(account);
                newCart.setProduct(product);
                newCart.setQuantity(1);
                newCart.setCreatedDate(LocalDate.now());
                cartDAO.save(newCart);
            }
        }
//        return "redirect:/cart";
        return "redirect:/home";
    }


    @PostMapping("/update")
    public String updateCart(@RequestParam("cartId") Long cartId,
                             @RequestParam("quantity") int quantity) {
        Optional<Cart> cartOptional = cartDAO.findById(cartId);
        if (cartOptional.isPresent() && quantity > 0) {
            Cart cart = cartOptional.get();
            cart.setQuantity(quantity);
            cartDAO.save(cart);
        } else if (cartOptional.isPresent() && quantity <= 0) {
            cartDAO.deleteById(cartId);
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String remove(@RequestParam("productId") Long productId, HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null)
        	return "redirect:/account/login";
        Account account = accountDAO.findByEmail(email).stream().findFirst().orElse(null);
        if (account != null) {
        	cartDAO.deleteByAccount_AccountIDAndProduct_ProductID(account.getAccountID(), productId);
        }
        return "redirect:/cart";
    }
}


