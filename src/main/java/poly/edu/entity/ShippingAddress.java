package poly.edu.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ShippingAddress")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fullName;
	private String phone;
	private String address;
	private Boolean isDefault = false;

	@ManyToOne
	@JoinColumn(name = "AccountID")
	private Account account;
}
