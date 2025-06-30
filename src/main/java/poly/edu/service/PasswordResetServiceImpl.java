package poly.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import poly.edu.dao.PasswordResetDAO;
import poly.edu.entity.Account;
import poly.edu.entity.PasswordReset;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
   @Autowired
    private PasswordResetDAO resetDAO;

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
}
