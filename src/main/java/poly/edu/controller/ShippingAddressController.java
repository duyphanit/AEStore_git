package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import poly.edu.entity.Account;
import poly.edu.entity.ShippingAddress;
import poly.edu.service.AccountService;
import poly.edu.service.ShippingAddressService;

import java.util.List;

@RestController
@RequestMapping("/shipping-addresses")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService service;

    @Autowired
    private AccountService accountService;

    // ✅ Lấy danh sách địa chỉ giao hàng của người dùng đang đăng nhập
    @GetMapping
    public List<ShippingAddress> getAddresses(HttpSession session) {
        Account account = getCurrentAccount(session);
        return service.getByAccount(account.getId());
    }

    // ✅ Thêm hoặc cập nhật địa chỉ giao hàng
    @PostMapping
    public List<ShippingAddress> addOrUpdate(@RequestBody ShippingAddress address, HttpSession session) {
        Account account = getCurrentAccount(session);
        address.setAccount(account);
        service.save(address);
        return service.getByAccount(account.getId());
    }

    // ✅ Xoá địa chỉ theo ID
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ✅ Helper: Lấy tài khoản từ session
    private Account getCurrentAccount(HttpSession session) {
        String email = (String) session.getAttribute("email");
        if (email == null) {
            throw new RuntimeException("Chưa đăng nhập.");
        }

        Account account = accountService.findByEmail(email);
        if (account == null) {
            throw new RuntimeException("Không tìm thấy tài khoản với email: " + email);
        }

        return account;
    }
}
