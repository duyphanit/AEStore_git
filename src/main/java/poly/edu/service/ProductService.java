package poly.edu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public List<Product> findAll() {
		return productDAO.findAll();
	}
	
	public List<Product> findByCategoryId(Long CategoryId){
		return productDAO.findByCategory_Id(CategoryId);
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
