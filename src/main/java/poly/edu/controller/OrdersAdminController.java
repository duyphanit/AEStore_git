package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import poly.edu.dao.OrdersDAO;
import poly.edu.entity.Account;
import poly.edu.entity.Orders;
import poly.edu.service.NotificationService;
import poly.edu.service.OrderService;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrdersAdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrdersDAO ordersDAO;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Trang quản lý đơn hàng
    @GetMapping
    public String deliveryStatusPage(Model model) {
        List<Orders> orders = ordersDAO.findAll();

        // Sắp xếp: Chờ xử lý lên đầu, sau đó theo mã đơn hàng giảm dần
        orders.sort((o1, o2) -> {
            int s1 = getStatusOrder(o1.getStatus());
            int s2 = getStatusOrder(o2.getStatus());
            if (s1 != s2) return Integer.compare(s1, s2);
            return Long.compare(o2.getOrderID(), o1.getOrderID());
        });

        model.addAttribute("orders", orders);
        model.addAttribute("statuses", List.of("Chờ xử lý", "Đang giao hàng", "Giao hàng thành công"));
        return "admin/delivery_status";
    }

    private int getStatusOrder(String status) {
        return switch (status) {
            case "Chờ xử lý" -> 0;
            case "Đang giao hàng" -> 1;
            case "Giao hàng thành công" -> 2;
            default -> 3;
        };
    }

    // ✅ Cập nhật trạng thái đơn hàng
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("orderId") Long id,
                               @RequestParam("status") String status,
                               RedirectAttributes ra) {
        Orders order = orderService.findById(id);

        if (order == null) {
            ra.addFlashAttribute("alertType", "danger");
            ra.addFlashAttribute("message", "Không tìm thấy đơn hàng.");
        } else if (!"Giao hàng thành công".equals(order.getStatus())) {
            order.setStatus(status);
            orderService.save(order);

            // ✅ Nếu chuyển sang "Đang giao hàng", gửi thông báo
            if ("Đang giao hàng".equals(status)) {
                Account user = order.getAccount();

                notificationService.sendNotificationToUser(
                	    user.getId(), // ✅ đúng getter
                	    "Đơn hàng #" + order.getOrderID() + " đang được giao",
                	    "Cảm ơn bạn đã đặt hàng!",
                	    order.getOrderID()
                	);

                // ✅ Gửi WebSocket tới user
                messagingTemplate.convertAndSend("/topic/user/" + user.getFullname(), "new");
            }

            ra.addFlashAttribute("alertType", "success");
            ra.addFlashAttribute("message", "Cập nhật trạng thái thành công.");
        } else {
            ra.addFlashAttribute("alertType", "warning");
            ra.addFlashAttribute("message", "Đơn hàng đã giao thành công, không thể chỉnh sửa.");
        }

        return "redirect:/admin/orders";
    }

    // ✅ Xem chi tiết đơn hàng
    @GetMapping("/detail/{id}")
    public String viewOrderDetail(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Orders order = orderService.findById(id);
        if (order == null) {
            ra.addFlashAttribute("alertType", "danger");
            ra.addFlashAttribute("message", "Không tìm thấy đơn hàng.");
            return "redirect:/admin/orders";
        }

        model.addAttribute("order", order);
        return "admin/order_detail";
    }
}
