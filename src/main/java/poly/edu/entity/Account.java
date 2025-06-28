package poly.edu.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Account")

public class Account implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name = "accountID")
	private Long accountID;

	@NotBlank(message = "Email không được để trống")
	private String email;
	@NotBlank(message = "Mật khẩu không được để trống")
	private String password;
	@NotBlank(message = "Họ tên không được để trống")
	private String fullname;
	@NotBlank(message = "Số điện thoại không được để trống")
	private String phone;
	@NotBlank(message = "Địa chỉ không được để trống")
	private String address;
	private boolean role = false;
    
    @OneToMany(mappedBy = "account")
    private List<Cart> carts;
	
    @OneToMany(mappedBy = "account")
    private List<Orders> orders;
}
