package com.gameshop.www.eCommerce.cart.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gameshop.www.eCommerce.product.model.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "cart_item")
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @JsonBackReference
    @ManyToOne
    @JoinColumn
    private Cart cart;

}
