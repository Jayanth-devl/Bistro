package com.project.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.project.order.client.MenuClient;
import com.project.order.dto.MenuResponse;
import com.project.order.entity.Order;
import com.project.order.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuClient menuClient;

    public OrderService(OrderRepository orderRepository, MenuClient menuClient) {
        this.orderRepository = orderRepository;
        this.menuClient = menuClient;
    }

    public Order createOrder(Order order) {

        MenuResponse menu = menuClient.getMenuById(order.getMenuId());

        if (menu == null || Boolean.FALSE.equals(menu.getAvailable())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Menu item is sold out"
            );
        }

        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("PLACED");
        }

        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order updateOrderStatus(Long id, String status) {
        Order existingOrder = getOrderById(id);
        existingOrder.setStatus(status);
        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}