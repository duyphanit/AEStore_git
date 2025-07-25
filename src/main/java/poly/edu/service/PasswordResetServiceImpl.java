package poly.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import poly.edu.dao.PasswordResetDAO;
import poly.edu.entity.Account;
import poly.edu.entity.PasswordReset;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
   @Autowired
   private PasswordResetDAO resetDAO;
   
   @Autowired
   private AccountService accountService;
   
   @Autowired
   private EmailService emailService;
   
// Map lưu email -> danh sách thời điểm gửi (milisecond)
   private final Map<String, List<Long>> emailSendTimes = new ConcurrentHashMap<>();

    @Override
    public PasswordReset createToken(Account account) {
        // Xóa token cũ nếu có
        resetDAO.findByAccount(account).ifPresent(resetDAO::delete);

        PasswordReset reset = new PasswordReset();
        reset.setToken(UUID.randomUUID().toString());
        reset.setExpiryDate(LocalDateTime.now().plusMinutes(5)); // Token có hiệu lực 5 phút
        reset.setAccount(account);
        return resetDAO.save(reset);
    }

    @Override
    public boolean isValidToken(String token) {
        Optional<PasswordReset> opt = resetDAO.findByToken(token);
        return opt.isPresent() && opt.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    @Override
    public Account getAccountByToken(String token) {
        return resetDAO.findByToken(token)
                .filter(t -> t.getExpiryDate().isAfter(LocalDateTime.now()))
                .map(PasswordReset::getAccount)
                .orElse(null);
    }

    @Override
    public void deleteToken(String token) {
        resetDAO.findByToken(token).ifPresent(resetDAO::delete);
    }

    // Reset số lần gửi khi nhập email mới
	public void resetEmailAttempts(String email) {
		emailSendTimes.remove(email);
	}

	// Kiểm tra có được phép gửi mail không, nếu không trả về thời gian chờ còn lại (ms)
	public long getCooldownTime(String email) {
		long now = System.currentTimeMillis();
        //Object emailSendTimes;
		List<Long> times = emailSendTimes.getOrDefault(email, new ArrayList<>());
        times.removeIf(t -> now - t > 30 * 1000); // Cooldown 30 giây
        if (times.isEmpty()) return 0;
        long last = times.get(times.size() - 1);
        if (now - last < 30 * 1000) {
            return (30 * 1000) - (now - last);
        }
		return 0;
	}

	// Ghi nhận lần gửi mới
    public void recordSend(String email) {
        long now = System.currentTimeMillis();
        List<Long> times = emailSendTimes.getOrDefault(email, new ArrayList<>());
        times.removeIf(t -> now - t > 15 * 60 * 1000);
        times.add(now);
        emailSendTimes.put(email, times);
    }

	@Override
	public void sendResetEmail(String email) {
		Account account = accountService.findByEmail(email);
        if (account == null) return;
        PasswordReset reset = createToken(account);
        String resetLink = "http://localhost:8080/reset-password?token=" + reset.getToken();
        String subject = "Yêu cầu đặt lại mật khẩu";
        String body = "Nhấn vào link sau để đặt lại mật khẩu: " + resetLink;
        emailService.sendMail(email, subject, body);		
	}

	@Override
	public int getSendCount(String email) {
		long now = System.currentTimeMillis();
        List<Long> times = emailSendTimes.getOrDefault(email, new ArrayList<>());
        times.removeIf(t -> now - t > 15 * 60 * 1000);
        emailSendTimes.put(email, times);
        return times.size();
	}

	@Override
	public long getBlockTime(String email) {
		long now = System.currentTimeMillis();
        List<Long> times = emailSendTimes.getOrDefault(email, new ArrayList<>());
        times.removeIf(t -> now - t > 15 * 60 * 1000);
        if (times.size() < 3) return 0; // Chỉ block khi đã gửi >= 3 lần trong 15 phút
        long first = times.get(0);
        // Nếu đã gửi 3 lần, block 15 phút kể từ lần gửi đầu tiên
        if (now - first < 15 * 60 * 1000) {
            return (15 * 60 * 1000) - (now - first);
        }
        return 0;
    }
}
