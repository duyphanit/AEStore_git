package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import poly.edu.entity.Account;
import poly.edu.service.AccountService;

@Controller
@RequestMapping("/admin/accounts")
public class AdminAccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public String list(@RequestParam(value = "message", required = false) String message, Model model) {
        model.addAttribute("account", new Account()); // form rỗng (thêm mới)
        model.addAttribute("accounts", accountService.findAll());

        if (message != null) {
            model.addAttribute("message", message);
        }

        return "admin/account_crud";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("account") Account acc,
                       BindingResult result,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("accounts", accountService.findAll());
            model.addAttribute("alertType", "danger");
            model.addAttribute("message", "❌ Vui lòng nhập đầy đủ thông tin!");
            return "admin/account_crud";
        }
        accountService.save(acc);
        model.addAttribute("account", new Account()); // Reset form
        model.addAttribute("accounts", accountService.findAll());
        model.addAttribute("alertType", "success");
        model.addAttribute("message", "✅ Lưu tài khoản thành công!");
        return "admin/account_crud";
    }


    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Account acc = accountService.findById(id);
        if (acc != null) {
            model.addAttribute("account", acc);
            model.addAttribute("alertType", "info");
            model.addAttribute("message", "ℹ️ Đang chỉnh sửa tài khoản: " + acc.getEmail());
        } else {
            model.addAttribute("account", new Account());
            model.addAttribute("alertType", "danger");
            model.addAttribute("message", "❌ Không tìm thấy tài khoản!");
        }
        model.addAttribute("accounts", accountService.findAll());
        return "admin/account_crud";
    }


    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        accountService.deleteById(id);
        model.addAttribute("account", new Account());
        model.addAttribute("accounts", accountService.findAll());
        model.addAttribute("alertType", "warning");
        model.addAttribute("message", "🗑️ Đã xóa tài khoản!");
        return "admin/account_crud";
    }

}
