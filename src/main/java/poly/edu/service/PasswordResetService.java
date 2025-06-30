package poly.edu.service;

import poly.edu.entity.*;

public interface PasswordResetService {
    PasswordReset createToken(Account account);
    boolean isValidToken(String token);
    Account getAccountByToken(String token);
    void deleteToken(String token);
}
