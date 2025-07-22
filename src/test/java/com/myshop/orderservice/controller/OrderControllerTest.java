package com.myshop.orderservice.controller;

import com.myshop.orderservice.entity.Order;
import com.myshop.orderservice.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    OrderService orderService;

    @InjectMocks
    OrderController orderController;

    @Test
    public void createOrderTest(){

        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");

        when(orderService.createOrder(order)).thenReturn(order);

        ResponseEntity<Order> response = orderController.createOrder(order);

        // Assert
        assertNotNull(response);
        assertEquals(order, response.getBody());

        // Verify interaction
        verify(orderService, times(1)).createOrder(order);
    }

    @Test
    public void placeOrderTest(){
        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");

        when(orderService.placeOrder(1l,4)).thenReturn(order);

        ResponseEntity<Order> resposeOrder = orderController.placeOrder(1l,4);

        assertEquals(order,resposeOrder.getBody());
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

        when(orderService.getAllOrders()).thenReturn(allOrders);

        List<Order> responseList = orderController.getAllOrders();

        assertEquals(allOrders,responseList);
    }

    @Test
    public void getOrderByIdTest(){
        Order order = new Order();
        order.setId(1l);
        order.setProductId(1l);
        order.setQuantity(4);
        order.setTotalPrice(200.00);
        order.setStatus("Deliverd");

        Optional<Order> optionalOrder = Optional.of(order);

        when(orderService.getOrderById(1l)).thenReturn(optionalOrder);

        ResponseEntity<Order> response = orderController.getOrderById(1l);

        assertEquals(order,response.getBody());
        assertEquals(HttpStatusCode.valueOf(200),response.getStatusCode());
    }

    @Test
    public void cancelOrderTest(){

        orderController.cancelOrder(1l);

        verify(orderService, times(1)).cancelOrder(1l);
    }

}