package com.myshop.orderservice.service;

import com.myshop.orderservice.entity.Order;
import com.myshop.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Order placeOrder(Long productId, int quantity) {
        // Call Product Service to validate stock
        String productServiceUrl = "http://localhost:8081/api/products/" + productId;
        Product product = restTemplate.getForObject(productServiceUrl, Product.class);

        if (product == null) {
            throw new RuntimeException("Product not found.");
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Insufficient stock.");
        }

        // Reduce stock via Product Service
        restTemplate.put(productServiceUrl + "/reduce-stock?quantity=" + quantity, null);

        // Save Order
        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice() * quantity);
        return orderRepository.save(order);
    }
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Optional: Call Product Service to increase stock back
        String productServiceUrl = "http://localhost:8081/api/products/" + order.getProductId() + "/increase-stock?quantity=" + order.getQuantity();
        restTemplate.put(productServiceUrl, null);

        orderRepository.delete(order);
    }

}
