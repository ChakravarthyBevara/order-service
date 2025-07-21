package com.myshop.orderservice.controller;

import com.myshop.orderservice.entity.Order;
import com.myshop.orderservice.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order savedOrder = service.createOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestParam Long productId, @RequestParam int quantity) {
        Order order = service.placeOrder(productId, quantity);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return service.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return service.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        service.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

}
