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
@Table(name = "cartDetail")
public class CartDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cartId", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "productOptionId", nullable = false)
    private ProductOption productOption;

    private int cartCount;

    public void countUpDown(String type) {
        if(type.equals("up"))
            this.cartCount = cartCount + 1;
        else
            this.cartCount = cartCount < 2 ? 1 : cartCount - 1;


    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void addCartCount(int cartCount) {
        this.cartCount = this.cartCount + cartCount;
    }

    @Override
    public String toString() {
        return "CartDetail{" +
                "id=" + id +
                ", productOption=" + productOption +
                ", cartCount=" + cartCount +
                '}';
    }
}
