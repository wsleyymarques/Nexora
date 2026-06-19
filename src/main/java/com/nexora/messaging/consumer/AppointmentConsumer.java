package com.nexora.messaging.consumer;

import com.nexora.config.RabbitMQConfig;
import com.nexora.messaging.event.AppointmentCreatedEvent;
import com.nexora.messaging.event.AppointmentStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentConsumer {

    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_CREATED_QUEUE)
    public void handleAppointmentCreated(AppointmentCreatedEvent event) {
        log.info("[MQ] Received appointment.created → appointmentId={} scheduledAt={}",
                event.appointmentId(), event.scheduledAt());

    }

    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_STATUS_QUEUE)
    public void handleAppointmentStatusChanged(AppointmentStatusChangedEvent event) {
        log.info("[MQ] Received appointment.status → appointmentId={} {} → {}",
                event.appointmentId(), event.oldStatus(), event.newStatus());
    }
}