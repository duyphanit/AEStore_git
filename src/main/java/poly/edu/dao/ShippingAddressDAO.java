package poly.edu.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import poly.edu.entity.ShippingAddress;

public interface ShippingAddressDAO extends JpaRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByAccount_Id(Long accountId);
    Optional<ShippingAddress> findByAccount_IdAndIsDefaultTrue(Long accountId);
}