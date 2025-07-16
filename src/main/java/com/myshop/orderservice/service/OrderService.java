package com.myshop.orderservice.service;

import com.myshop.orderservice.entity.Order;
import com.myshop.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service

public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    private final RestTemplate restTemplate;

    public Order placeOrder(Order order) {
        String productUrl = "http://localhost:8081/api/products/" + order.getProductId();
        Product product = restTemplate.getForObject(productUrl, Product.class);

        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        restTemplate.put(productUrl + "/reduce-stock?quantity=" + order.getQuantity(), null);

        order.setTotalPrice(product.getPrice() * order.getQuantity());
        order.setStatus("PLACED");
        return repository.save(order);
    }
}

