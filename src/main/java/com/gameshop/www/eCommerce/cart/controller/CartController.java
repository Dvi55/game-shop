package com.gameshop.www.eCommerce.cart.controller;

import com.gameshop.www.eCommerce.cart.model.Cart;
import com.gameshop.www.eCommerce.cart.model.CartBody;
import com.gameshop.www.eCommerce.cart.model.dto.CartDto;
import com.gameshop.www.eCommerce.cart.service.CartService;
import com.gameshop.www.eCommerce.user.model.LocalUser;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    private final CartService cartService;


    @PostMapping("/add")
    public ResponseEntity<CartDto> addToCart(@AuthenticationPrincipal LocalUser user, @RequestBody List<CartBody> cartBodyList) {
        CartDto cart = cartService.addProductToCart(user, cartBodyList);
        return ResponseEntity.ok(cart);
    }


    @DeleteMapping("/clear")
    public ResponseEntity<Cart> clearCart(@AuthenticationPrincipal LocalUser user) {
        Cart cart = cartService.clearCart(user);
        return ResponseEntity.ok(cart);
    }

    @GetMapping("/details")
    public ResponseEntity<CartDto> getCartDetails(@AuthenticationPrincipal LocalUser user) {
        CartDto cart = cartService.getCartDetails(user);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/remove")
    public ResponseEntity<CartDto> removeFromCart(@AuthenticationPrincipal LocalUser user, @RequestBody List<CartBody> cartBodies) {
        CartDto cart = cartService.removeProductFromCart(user, cartBodies);
        return ResponseEntity.ok(cart);
    }
}
