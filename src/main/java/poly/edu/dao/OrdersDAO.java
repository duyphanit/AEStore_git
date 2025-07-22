package poly.edu.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import poly.edu.entity.Orders;

import java.util.List;

public interface OrdersDAO extends JpaRepository<Orders, Long> {
    List<Orders> findByAccount_Email(String email);
    List<Orders> findByAccount_Id(Long id);
}
