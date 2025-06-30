package poly.edu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import poly.edu.entity.Account;
import poly.edu.service.AccountService;
import poly.edu.service.PasswordResetService;
import poly.edu.service.EmailService;
import poly.edu.entity.PasswordReset;

@Controller
public class ForgotPasswordController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Account account = accountService.findByEmail(email);
        if (account == null) {
            model.addAttribute("error", "Email không tồn tại!");
            return "account/login";
        }
        PasswordReset reset = passwordResetService.createToken(account);
        String resetLink = "http://localhost:8080/reset-password?token=" + reset.getToken();
        emailService.sendMail(account.getEmail(), "Đặt lại mật khẩu", "Nhấn vào link sau để đặt lại mật khẩu: " + resetLink);
        model.addAttribute("message", "Đã gửi email đặt lại mật khẩu!");
        return "account/login";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!passwordResetService.isValidToken(token)) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn!");
            return "account/login";
        }
        model.addAttribute("token", token);
        return "account/resetpassword";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("token", token);
            return "account/resetpassword";
        }
        Account account = passwordResetService.getAccountByToken(token);
        if (account == null) {
            model.addAttribute("error", "Token không hợp lệ hoặc đã hết hạn!");
            return "account/login";
        }
        account.setPassword(newPassword); // Nên mã hóa mật khẩu nếu có
        accountService.save(account);
        passwordResetService.deleteToken(token);
        model.addAttribute("message", "Đổi mật khẩu thành công!");
        return "account/login";
    }
}
