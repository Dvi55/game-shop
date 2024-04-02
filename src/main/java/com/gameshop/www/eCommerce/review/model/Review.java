package com.gameshop.www.eCommerce.review.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gameshop.www.eCommerce.product.model.Product;
import com.gameshop.www.eCommerce.user.model.LocalUser;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "rate", nullable = false)
    private Integer rate;

    @Column(name = "comment", length = 1000)
    private String comment;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "local_user_id", nullable = false)
    private LocalUser localUser;

}