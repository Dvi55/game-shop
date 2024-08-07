package com.gameshop.ecommerce.product.dao.projection;

import com.gameshop.ecommerce.product.dao.projection.catalog.BrandView;

import java.util.UUID;

public interface SearchView {
    UUID getId();

    String getName();

    String getShortDescription();

    BrandView getBrand();

    Integer getPrice();

    String getImageUrl();

    Integer getPriceWithSale();
}
