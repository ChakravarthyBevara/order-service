package com.myshop.orderservice.service;

import com.myshop.orderservice.entity.Order;
import com.myshop.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Order createOrder(Order order) {
        logger.info("Creating new order for product ID: {}", order.getProductId());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        logger.info("Fetching all orders");
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        logger.info("Fetching order with ID: {}", id);
        return orderRepository.findById(id);
    }

    public Order placeOrder(Long productId, int quantity) {
        logger.info("Placing order for product ID: {} with quantity: {}", productId, quantity);
        String productServiceUrl = "http://localhost:8081/api/products/" + productId;
        Product product = restTemplate.getForObject(productServiceUrl, Product.class);

        if (product == null) {
            logger.error("Product not found for ID: {}", productId);
            throw new RuntimeException("Product not found.");
        }

        if (product.getStock() < quantity) {
            logger.error("Insufficient stock for product ID: {}. Available: {}, Requested: {}", productId, product.getStock(), quantity);
            throw new RuntimeException("Insufficient stock.");
        }

        logger.info("Reducing stock for product ID: {} by quantity: {}", productId, quantity);
        restTemplate.put(productServiceUrl + "/reduce-stock?quantity=" + quantity, null);

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(product.getPrice() * quantity);
        Order savedOrder = orderRepository.save(order);
        logger.info("Order placed successfully. Order ID: {}", savedOrder.getId());
        return savedOrder;
    }

    public void cancelOrder(Long orderId) {
        logger.info("Cancelling order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.warn("Order with ID {} not found", orderId);
                    return new RuntimeException("Order not found");
                });

        logger.info("Restoring stock for cancelled order. Product ID: {}, Quantity: {}", order.getProductId(), order.getQuantity());
        String productServiceUrl = "http://localhost:8081/api/products/" + order.getProductId() + "/increase-stock?quantity=" + order.getQuantity();
        restTemplate.put(productServiceUrl, null);

        orderRepository.delete(order);
        logger.info("Order with ID: {} cancelled successfully", orderId);
    }
}
