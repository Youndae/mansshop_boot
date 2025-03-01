package com.example.mansshop_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @Column(length = 200)
    private String cookieId;

    @CreationTimestamp
    private LocalDate createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.PERSIST)
    private final List<CartDetail> cartDetailSet = new ArrayList<>();

    public void addCartDetail(CartDetail cartDetail) {
        cartDetailSet.add(cartDetail);
        cartDetail.setCart(this);
    }



}
