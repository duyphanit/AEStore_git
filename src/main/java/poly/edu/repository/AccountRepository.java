package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import poly.edu.entity.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String email);

    // ✅ tìm tất cả tài khoản là admin (Role = true)
    List<Account> findByRoleTrue();
}
