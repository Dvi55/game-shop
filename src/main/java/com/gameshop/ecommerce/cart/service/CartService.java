package com.gameshop.ecommerce.cart.service;

import com.gameshop.ecommerce.cart.model.Cart;
import com.gameshop.ecommerce.cart.model.dto.CartDto;
import com.gameshop.ecommerce.cart.model.dto.CartItemDto;
import com.gameshop.ecommerce.exception.ProductNotFoundException;
import com.gameshop.ecommerce.product.dto.ProductCatalogDTO;
import com.gameshop.ecommerce.user.dao.LocalUserDAO;
import com.gameshop.ecommerce.user.model.LocalUser;
import com.gameshop.ecommerce.cart.dao.CartDAO;
import com.gameshop.ecommerce.cart.model.CartBody;
import com.gameshop.ecommerce.cart.model.CartItem;
import com.gameshop.ecommerce.product.dao.ProductDAO;
import com.gameshop.ecommerce.product.model.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartService {
    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final LocalUserDAO localUserDAO;


    public Cart getCartByUser(LocalUser user) {
        return cartDAO.findByUser_Id(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartDAO.save(cart);
        });
    }

    @Transactional
    public CartDto addProductToCart(LocalUser user, List<CartBody> cartBodies) {
        Cart cart = getCartByUser(user);

        List<Product> products = productDAO.findAllById(cartBodies.stream()
                .map(CartBody::getId)
                .collect(Collectors.toList()));

        Map<UUID, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        for (CartBody cartBody : cartBodies) {
            Product product = productMap.get(cartBody.getId());
            if (product == null) {
                throw new ProductNotFoundException("Product not found with id: " + cartBody.getId());
            }

            Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                    .filter(cartItem -> cartItem.getProduct().equals(product))
                    .findFirst();

            if (existingCartItem.isPresent()) {
                CartItem cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + cartBody.getQuantity());
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setQuantity(cartBody.getQuantity());
                cartItem.setCart(cart);
                cart.getCartItems().add(cartItem);
            }
        }
        return convertToCartDto(cartDAO.save(cart));
    }

    @Transactional
    public Cart removeProductFromCart(LocalUser user, List<CartBody> cartBodies) {
        Cart cart = getCartByUser(user);

        Map<UUID, CartItem> cartItemMap = cart.getCartItems().stream()
                .collect(Collectors.toMap(cartItem -> cartItem.getProduct().getId(), cartItem -> cartItem));

        cartBodies.forEach(cartBody -> {
            CartItem cartItem = cartItemMap.get(cartBody.getId());
            if (cartItem != null) {
                int newQuantity = cartItem.getQuantity() - cartBody.getQuantity();
                if (newQuantity > 0) {
                    cartItem.setQuantity(newQuantity);
                } else {
                    cart.getCartItems().remove(cartItem);
                }
            }
        });

        return cartDAO.save(cart);
    }

    @Transactional
    public Cart clearCart(LocalUser user) {
        Cart cart = getCartByUser(user);
        cart.getCartItems().clear();
        return cartDAO.save(cart);
    }

    public CartDto getCartDetails(LocalUser user) {
        return convertToCartDto(getCartByUser(user));
    }

    private CartDto convertToCartDto(Cart cart) {
        return new CartDto(cart.getCartItems().stream()
                .map(this::createCartItemDto)
                .collect(Collectors.toSet()), cart.getId());
    }

    private CartItemDto createCartItemDto(CartItem cartItem) {
        ProductCatalogDTO productDto = new ProductCatalogDTO();
        productDto.setId(cartItem.getProduct().getId());
        productDto.setName(cartItem.getProduct().getName());
        productDto.setPrice(cartItem.getProduct().getPrice());
        productDto.setImageUrl(cartItem.getProduct().getImageUrl());
        productDto.setPriceWithSale(cartItem.getProduct().getPriceWithSale());
        return new CartItemDto(cartItem.getId(), productDto, cartItem.getQuantity());
    }
}
