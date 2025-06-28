package poly.edu.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import poly.edu.entity.Cart;

@Repository
public interface CartDAO extends JpaRepository<Cart, Long> {

    List<Cart> findByAccount_Email(String email);

    Optional<Cart> findByAccount_AccountIDAndProduct_ProductID(Long accountId, Long productId);

    @Transactional  // 🛠️ Bắt buộc để xử lý xóa
    void deleteByAccount_AccountIDAndProduct_ProductID(Long accountId, Long productId);
    
    //Đếm
    long countByAccount_AccountID(Long accountID);

}
