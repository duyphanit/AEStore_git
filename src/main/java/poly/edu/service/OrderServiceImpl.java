package poly.edu.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import poly.edu.dao.OrderDetailDAO;
import poly.edu.dao.OrdersDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Cart;
import poly.edu.entity.OrderDetail;
import poly.edu.entity.Orders;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersDAO ordersDAO;

    @Autowired
    private OrderDetailDAO orderDetailDAO;

    // ✅ Thêm dòng này để gửi WebSocket
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public Orders createOrder(Account account, List<Cart> carts, String paymentMethod, String shippingMethod) {
        Orders order = new Orders();
        order.setAccount(account);
        order.setDate(LocalDate.now());
        order.setStatus("Chờ xử lý");

        double sumTotal = carts.stream()
                .mapToDouble(cart -> cart.getProduct().getFirstPrice() * cart.getQuantity())
                .sum();
        order.setSumTotal(sumTotal);

        Orders savedOrder = ordersDAO.save(order);

        for (Cart cart : carts) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderID(savedOrder.getOrderID());
            detail.setProductID(cart.getProduct().getProductID());
            detail.setQuantity(cart.getQuantity());
            detail.setLastPrice(cart.getProduct().getFirstPrice());
            detail.setTotal(cart.getProduct().getFirstPrice() * cart.getQuantity());

            orderDetailDAO.save(detail);
        }

        // ✅ Gửi WebSocket thông báo tới admin
        messagingTemplate.convertAndSend("/topic/orders", savedOrder);

        return savedOrder;
    }

    @Override
    public List<Orders> findAll() {
        return ordersDAO.findAll();
    }

    @Override
    public Orders findById(Long id) {
        return ordersDAO.findById(id).orElse(null);
    }

    @Override
    public Orders save(Orders order) {
        return ordersDAO.save(order);
    }

    @Override
    public long getTotalOrders() {
        return ordersDAO.count();
    }

    @Override
    public double getTotalRevenue() {
        return ordersDAO.findAll().stream()
                .mapToDouble(Orders::getSumTotal)
                .sum();
    }
    @Override
    public long countOrdersByStatus(String status) {
        return ordersDAO.countByStatus(status);
    }
}
