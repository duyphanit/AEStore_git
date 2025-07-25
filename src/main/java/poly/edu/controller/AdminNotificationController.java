package poly.edu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import poly.edu.entity.Notification;
import poly.edu.service.NotificationService;

import java.util.List;

@Controller
@RequestMapping("/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * ✅ API trả về danh sách thông báo dạng JSON cho frontend (AJAX hoặc WebSocket nếu cần)
     */
    @GetMapping("/api")
    @ResponseBody
    public List<Notification> getNotificationsAPI() {
        return notificationService.getNotificationsForAdmin();
    }

    /**
     * ✅ Trang hiển thị danh sách thông báo cho admin
     */
    @GetMapping
    public String showNotifications(Model model) {
        List<Notification> notifications = notificationService.getNotificationsForAdmin();
        model.addAttribute("notifications", notifications);
        return "admin/notification"; // Giao diện: templates/admin/notification.html
    }
    @PostMapping("/mark-all-read")
    @ResponseBody
    public void markAllAsRead() {
        notificationService.markAllAdminNotificationsAsRead();
    }


    /**
     * ✅ Đánh dấu 1 thông báo đã đọc
     */
    @PostMapping("/mark-read/{id}")
    @ResponseBody
    public String markAsRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return "OK";
    }

    /**
     * ✅ API lấy danh sách thông báo chưa đọc
     */
    @GetMapping("/api/unread")
    @ResponseBody
    public List<Notification> getUnreadNotificationsAPI() {
        return notificationService.getUnreadNotificationsForAdmin();
    }
}
