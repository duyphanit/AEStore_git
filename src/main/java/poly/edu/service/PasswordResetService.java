package poly.edu.service;

import poly.edu.entity.*;

public interface PasswordResetService {
	PasswordReset createToken(Account account);

	boolean isValidToken(String token);

	Account getAccountByToken(String token);

	void deleteToken(String token);

	void resetEmailAttempts(String email);

	long getCooldownTime(String email);

	void recordSend(String email);

	void sendResetEmail(String email);

	int getSendCount(String email);

	long getBlockTime(String email);
}
