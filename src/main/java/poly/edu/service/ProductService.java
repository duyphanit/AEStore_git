package poly.edu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import poly.edu.dao.CategoryDAO;
import poly.edu.dao.ProductDAO;
import poly.edu.entity.Category;
import poly.edu.entity.Product;

@Service
public class ProductService {
	@Autowired
	private ProductDAO productDAO;
	@Autowired
	private CategoryDAO categoryDAO;
	public long getTotalProducts() {
	    return productDAO.count();
	}


	public List<Product> findAll() {
		return productDAO.findAll();
	}
	
	public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
		return productDAO.findByCategory_Id(categoryId, pageable);
	}

	public Page<Product> findAll(Pageable pageable) {
		return productDAO.findAll(pageable);
	}

	public List<Category> findAllCategories() {
		return categoryDAO.findAll(); 
	}
	
//	CRUD_Product
	public Product save(Product pro) {
		return productDAO.save(pro);
		
	}
	
	public Product findById(Long id) {
		return productDAO.findById(id).orElse(null);
	}
	
	public void deleteById(Long id) {
		productDAO.deleteById(id);
	}
	
}
