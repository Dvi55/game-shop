package com.gameshop.ecommerce.wishlist.controller;

import com.gameshop.ecommerce.user.store.LocalUserEntity;
import com.gameshop.ecommerce.wishlist.store.WishListEntity;
import com.gameshop.ecommerce.wishlist.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/wishlist")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<Object> addProductToWishlist(@PathVariable UUID productId,
                                                       @AuthenticationPrincipal LocalUserEntity user) {
        if (user != null) {
            wishlistService.addProductToWishlist(productId, user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Product added to wishlist " + user.getEmail());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Object> removeProductFromWishlist(@PathVariable UUID productId,
                                                            @AuthenticationPrincipal LocalUserEntity user) {
        if (user != null) {
            wishlistService.removeProductFromWishlist(productId, user);
            return ResponseEntity.status(HttpStatus.OK).body("Product removed from wishlist " + user.getEmail());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getWishlist(@AuthenticationPrincipal LocalUserEntity user) {
        if (user != null) {
            List<WishListEntity> wishListEntity = wishlistService.getUserWishlist(user);
            return ResponseEntity.status(HttpStatus.OK).body(wishListEntity);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
