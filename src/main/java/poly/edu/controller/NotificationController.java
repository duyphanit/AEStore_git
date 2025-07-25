package poly.edu.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import poly.edu.entity.Notification;
import poly.edu.service.NotificationService;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // ✅ 1. Hiển thị danh sách thông báo
    @GetMapping
    public String list(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String role = (String) session.getAttribute("role");

        if (userId == null || role == null) {
            return "redirect:/account/login";
        }

        model.addAttribute("notifications", notificationService.getNotificationsForUser(userId));

        if ("ADMIN".equalsIgnoreCase(role)) {
            return "admin/notification";
        } else {
            return "user/notification";
        }
    }

    // ✅ 2. Đánh dấu đã đọc 1 thông báo
    @PostMapping("/mark-as-read")
    @ResponseBody
    public String markAsRead(@RequestParam("id") Long id) {
        try {
            notificationService.markAsRead(id);
            return "OK";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    // ✅ 3. API lấy danh sách chưa đọc
    @GetMapping("/api/unread")
    @ResponseBody
    public List<Notification> getUnreadNotifications(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return List.of();
        return notificationService.getUnreadNotificationsForUser(userId);
    }

    // ✅ 4. Điều hướng khi bấm vào thông báo (client gọi redirect tới đây)
    @PostMapping("/redirect")
    @ResponseBody
    public String redirectFromNotification(@RequestParam("id") Long notificationId, HttpSession session) {
        Notification noti = notificationService.findById(notificationId);
        if (noti == null || noti.getOrder() == null || noti.getReceiver() == null) {
            return "/account/profile"; // fallback
        }

        // Mark as read
        notificationService.markAsRead(notificationId);

        // Nếu là admin thì chuyển tới trang quản lý đơn
        String role = (String) session.getAttribute("role");
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "/admin/orders";
        }

        // Nếu là người dùng thì chuyển tới trang lịch sử + focusOrderId
        Long orderId = noti.getOrder().getOrderID();
        return "/account/profile?focusOrderId=" + orderId;
    }
}
