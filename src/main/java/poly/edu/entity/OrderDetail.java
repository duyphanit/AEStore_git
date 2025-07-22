package poly.edu.entity;

import java.io.Serializable;

<<<<<<< HEAD
=======
import jakarta.persistence.Column;
>>>>>>> 01796f7 (uppdate Order_history)
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "OrderDetail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OrderDetailId.class)
public class OrderDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long orderID;

	@Id
<<<<<<< HEAD
	private Long productID;

	private Integer quantity;
=======
	@Column(name = "Product_ID")
	private Long productID;

	private Integer quantity;
	
	@Column(name = "Last_Price")
>>>>>>> 01796f7 (uppdate Order_history)
	private Double lastPrice;
	private Double total;

	@ManyToOne
	@JoinColumn(name = "orderID", insertable = false, updatable = false)
	private Orders order;

	@ManyToOne
<<<<<<< HEAD
	@JoinColumn(name = "productID", insertable = false, updatable = false)
=======
	@JoinColumn(name = "Product_ID", insertable = false, updatable = false)
>>>>>>> 01796f7 (uppdate Order_history)
	private Product product;
}
