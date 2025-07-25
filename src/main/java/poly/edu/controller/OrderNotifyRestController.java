package poly.edu.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import poly.edu.dao.OrdersDAO;
import poly.edu.service.OrderService;

@RestController
@RequestMapping("/admin/api/orders")
public class OrderNotifyRestController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/new-count")
    public Map<String, Object> getNewOrderCount() {
        long count = orderService.countOrdersByStatus("Chờ xác nhận");
        return Collections.singletonMap("count", count);
    }
}

