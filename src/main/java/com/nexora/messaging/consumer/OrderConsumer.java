package com.nexora.messaging.consumer;

import com.nexora.config.RabbitMQConfig;
import com.nexora.messaging.event.OrderCreatedEvent;
import com.nexora.messaging.event.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderConsumer {

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("[MQ] Received order.created → orderId={} customer={}",
                event.orderId(), event.customerName());
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_STATUS_QUEUE)
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("[MQ] Received order.status → orderId={} {} → {}",
                event.orderId(), event.oldStatus(), event.newStatus());
    }
}