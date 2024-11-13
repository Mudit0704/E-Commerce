package com.ecom.microservices.order_service.service;

import com.ecom.microservices.order_service.dto.OrderRequest;
import com.ecom.microservices.order_service.model.Order;
import com.ecom.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest order) {
        Order newOrder = new Order();
        newOrder.setOrderNumber(UUID.randomUUID().toString());
        newOrder.setPrice(order.price());
        newOrder.setSkuCode(order.skuCode());
        newOrder.setQuantity(order.quantity());

        orderRepository.save(newOrder);
    }
}
