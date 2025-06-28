package poly.edu.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import poly.edu.dao.CartDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Cart;
import poly.edu.entity.Product;

@Service
public class cartService {

    @Autowired
    private CartDAO cartDAO;

    public void addToCart(Account account, Product product) {
        Optional<Cart> optionalCart = cartDAO.findByAccount_AccountIDAndProduct_ProductID(account.getAccountID(), product.getProductID());

        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.setQuantity(cart.getQuantity() + 1);
            cartDAO.save(cart);
        } else {
//          Cart cart = new Cart(null, account, product, 1, LocalDate.now());
            Cart cart = new Cart();
            cartDAO.save(cart);
        }
    }

    public List<Cart> getCartByEmail(String email) {
        return cartDAO.findByAccount_Email(email);
    }

    public void removeItem(Long accountId, Long productId) {
        cartDAO.deleteByAccount_AccountIDAndProduct_ProductID(accountId, productId);
    }

    public void updateQuantity(Long cartId, int newQuantity) {
        Cart cart = cartDAO.findById(cartId).orElseThrow();
        if (newQuantity > 0) {
            cart.setQuantity(newQuantity);
            cartDAO.save(cart);
        } else {
            cartDAO.deleteById(cartId); // nếu giảm về 0 thì xóa khỏi giỏ
        }
    }
    
    public int Count_Cart(Long accountID) {
    	return (int) cartDAO.countByAccount_AccountID(accountID);
    }
}