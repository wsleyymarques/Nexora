package com.nexora.messaging.producer;

import com.nexora.config.RabbitMQConfig;
import com.nexora.messaging.event.AppointmentCreatedEvent;
import com.nexora.messaging.event.AppointmentStatusChangedEvent;
import com.nexora.messaging.event.OrderCreatedEvent;
import com.nexora.messaging.event.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        log.info("[MQ] Publishing order.created → orderId={}", event.orderId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_KEY,
                event
        );
    }

    public void publishOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("[MQ] Publishing order.status → orderId={} status={}", event.orderId(), event.newStatus());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDERS_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_KEY,
                event
        );
    }

    public void publishAppointmentCreated(AppointmentCreatedEvent event) {
        log.info("[MQ] Publishing appointment.created → appointmentId={}", event.appointmentId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.APPOINTMENTS_EXCHANGE,
                RabbitMQConfig.APPOINTMENT_CREATED_KEY,
                event
        );
    }

    public void publishAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        log.info("[MQ] Publishing appointment.status → appointmentId={} status={}", event.appointmentId(), event.newStatus());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.APPOINTMENTS_EXCHANGE,
                RabbitMQConfig.APPOINTMENT_STATUS_KEY,
                event
        );
    }
}