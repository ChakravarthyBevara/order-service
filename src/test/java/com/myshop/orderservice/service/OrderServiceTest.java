package com.myshop.orderservice.service;

import com.myshop.orderservice.entity.Order;
import com.myshop.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService;

    @Test
    public void createOrderTest() {
        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");

        when(orderRepository.save(order)).thenReturn(order);
        Order response = orderService.createOrder(order);

        assertEquals(order, response);
    }

    @Test
    public void getAllOrdersTest(){
        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");

        Order order1 = new Order();
        order1.setId(2l);
        order1.setProductId(2l);
        order1.setQuantity(4);
        order1.setTotalPrice(200.00);
        order1.setStatus("Deliverd");

        List<Order> allOrders= new ArrayList<>();
        allOrders.add(order);
        allOrders.add(order1);

        when(orderRepository.findAll()).thenReturn(allOrders);
        List<Order> response = orderService.getAllOrders();

        assertEquals(allOrders,response);
    }

    @Test
    public void getOrderByIdTest(){
        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");
        Optional optional = Optional.of(order);

        when(orderRepository.findById(1l)).thenReturn(optional);

        Optional<Order> response = orderService.getOrderById(1l);

        assertEquals(optional,response);
    }

    @Test
    public void placeOrderTest(){
        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");

        long productId = 1;
        int quantity = 4;

        Product product = new Product();
        product.setId(productId);
        product.setName("Shoes");
        product.setDescription("Very good shes");
        product.setPrice(2500);
        product.setStock(25);

        when(restTemplate.getForObject("http://localhost:8081/api/products/1", Product.class)).thenReturn(product);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order response = orderService.placeOrder(productId,quantity);

        assertEquals(order.getId(),response.getId());
    }

    @Test
    public void placeOrderTestWhenProductIsNull(){
        long productId = 1;
        int quantity = 4;

        when(restTemplate.getForObject("http://localhost:8081/api/products/1", Product.class)).thenReturn(null);

        assertThrows(RuntimeException.class,()->{
            orderService.placeOrder(productId,quantity);
        });
    }

    @Test
    public void placeOrderTestWhenInsufficientStock(){
        long productId = 1;
        int quantity = 4;

        Product product = new Product();
        product.setId(productId);
        product.setName("Shoes");
        product.setDescription("Very good shes");
        product.setPrice(2500);
        product.setStock(2);

        when(restTemplate.getForObject("http://localhost:8081/api/products/1", Product.class)).thenReturn(product);

        assertThrows(RuntimeException.class,()->{
            orderService.placeOrder(productId,quantity);
        });
    }

    @Test
    public void cancelOrderTest(){
        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");
        Optional optional = Optional.of(order);

        when(orderRepository.findById(1l)).thenReturn(optional);

        orderService.cancelOrder(1l);

        verify(orderRepository, times(1)).findById(1l);
    }

    @Test
    public void cancelOrderTestWhenOrderNotFound(){
        Optional optional = Optional.empty();

        when(orderRepository.findById(1l)).thenReturn(optional);

        assertThrows(RuntimeException.class,()->{
            orderService.cancelOrder(1l);
        });
    }
}