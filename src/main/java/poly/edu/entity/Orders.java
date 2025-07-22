package poly.edu.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

<<<<<<< HEAD
=======
import jakarta.persistence.Column;
>>>>>>> 01796f7 (uppdate Order_history)
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable{
<<<<<<< HEAD
	
=======
>>>>>>> 01796f7 (uppdate Order_history)
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private Long orderID;

    private LocalDate date;
    private String status;
    private Double sumTotal;

    @ManyToOne
    @JoinColumn(name = "accountID")
=======
	@Column(name = "OrderID")
    private Long orderID;

	@Column(name = "Date")
    private LocalDate date;
    
	@Column(name = "Status")
    private String status;
	
	@Column(name = "SumTotal")
    private Double sumTotal;

    @ManyToOne
    @JoinColumn(name = "AccountID")
>>>>>>> 01796f7 (uppdate Order_history)
    private Account account;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order")
    private List<Payment> payments;
}
