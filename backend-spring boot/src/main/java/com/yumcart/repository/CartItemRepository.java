package com.yumcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yumcart.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {


//    CartItem findByFoodIsContaining

}
