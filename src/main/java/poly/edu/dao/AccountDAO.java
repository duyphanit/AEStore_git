package poly.edu.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.Account;

public interface AccountDAO extends JpaRepository<Account, Long> {
	Optional<Account> findByEmail(String email);
    boolean existsByEmail(String email);
}
