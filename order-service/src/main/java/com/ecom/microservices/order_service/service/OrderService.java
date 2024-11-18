package com.ecom.microservices.order_service.service;

import com.ecom.microservices.order_service.client.InventoryClient;
import com.ecom.microservices.order_service.dto.OrderRequest;
import com.ecom.microservices.order_service.event.OrderPlacedEvent;
import com.ecom.microservices.order_service.model.Order;
import com.ecom.microservices.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest order) {
        boolean isProductInStock = inventoryClient.isInStock(order.skuCode(), order.quantity());

        if (isProductInStock) {
            Order newOrder = new Order();
            newOrder.setOrderNumber(UUID.randomUUID().toString());
            newOrder.setPrice(order.price());
            newOrder.setSkuCode(order.skuCode());
            newOrder.setQuantity(order.quantity());

            orderRepository.save(newOrder);

            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
            orderPlacedEvent.setOrderNumber(newOrder.getOrderNumber());
            orderPlacedEvent.setEmail(order.userDetails().email());
            orderPlacedEvent.setFirstName(order.userDetails().firstName());
            orderPlacedEvent.setLastName(order.userDetails().lastName());

            kafkaTemplate.send("order-placed", orderPlacedEvent);
        } else {
            throw new RuntimeException("Product"+ order.skuCode() +"is not in stock");
        }
    }
}
