package poly.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.edu.entity.Orders;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
}
