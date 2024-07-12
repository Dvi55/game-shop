package com.gameshop.ecommerce.cart.model.dto;

import com.gameshop.ecommerce.cart.model.CartItem;
import com.gameshop.ecommerce.product.dto.ProductCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link CartItem}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto implements Serializable {
    private UUID id;
    private ProductCatalogDTO product;
    private Integer quantity;
}