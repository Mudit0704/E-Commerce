package com.ecom.microservices.order_service.service;

import com.ecom.microservices.order_service.client.InventoryClient;
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
    private final InventoryClient inventoryClient;

    public void placeOrder(OrderRequest order) {
        boolean isProductInStock = inventoryClient.isInStock(order.skuCode(), order.quantity());

        if (isProductInStock) {
            Order newOrder = new Order();
            newOrder.setOrderNumber(UUID.randomUUID().toString());
            newOrder.setPrice(order.price());
            newOrder.setSkuCode(order.skuCode());
            newOrder.setQuantity(order.quantity());

            orderRepository.save(newOrder);
        } else {
            throw new RuntimeException("Product"+ order.skuCode() +"is not in stock");
        }
    }
}
