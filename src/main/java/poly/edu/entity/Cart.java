package poly.edu.entity;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartID;

    @ManyToOne
    @JoinColumn(name = "Account_ID")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "Product_ID")
    private Product product;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "CreatedDate") 
    private LocalDate createdDate;

}
