package poly.edu.service;

import java.util.List;

import poly.edu.entity.Account;
import poly.edu.entity.Cart;
import poly.edu.entity.Orders;

public interface OrderService {
    Orders createOrder(Account account, List<Cart> selectedCarts, String paymentMethod, String shipMethod);

    // Admin
    List<Orders> findAll();
    Orders findById(Long id);
    Orders save(Orders order);

    // 🔥 THÊM VÀO ĐÂY
    long getTotalOrders();
    double getTotalRevenue();
    long countOrdersByStatus(String status);
}
