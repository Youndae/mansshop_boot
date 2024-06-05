package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cartId")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "productOptionId")
    private ProductOption productOption;

    private int cartCount;

    private int cartPrice;

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void countUpDown(String type) {
        int productPrice = productOption.getProduct().getProductPrice();

        if(type.equals("up")) {
            this.cartCount = cartCount + 1;
            this.cartPrice = cartPrice + productPrice;
        }else {
            this.cartCount = cartCount - 1;
            this.cartPrice = cartPrice - productPrice;
        }
    }
}
