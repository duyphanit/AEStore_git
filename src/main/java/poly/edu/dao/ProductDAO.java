package poly.edu.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import poly.edu.entity.Product;

public interface ProductDAO extends JpaRepository<Product, Long>{
	List<Product> findByCategory_Id(Long categoryId);
	List<Product> findByProductNameContainingIgnoreCase(String keyword);
	Page<Product> findByCategory_Id(Long categoryId, Pageable pageable);
}
