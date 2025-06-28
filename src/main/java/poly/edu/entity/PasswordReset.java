package poly.edu.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;

@Entity
@Table(name = "PasswordReset")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReset {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "token")
	private String token;
	
	@Column(name = "expiryDate")
	private LocalDateTime expiryDate;
    
	@OneToOne
	@JoinColumn(name = "account_id")
	private Account account;
}
