package com.project.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}