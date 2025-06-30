package poly.edu.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import poly.edu.entity.Account;
import poly.edu.entity.PasswordReset;

public interface PasswordResetDAO extends JpaRepository<PasswordReset, Long>{
	Optional<PasswordReset> findByToken(String token);
	Optional<PasswordReset> findByAccount(Account account);
}
