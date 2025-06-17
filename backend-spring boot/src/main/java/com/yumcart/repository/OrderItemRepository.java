package com.yumcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yumcart.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
