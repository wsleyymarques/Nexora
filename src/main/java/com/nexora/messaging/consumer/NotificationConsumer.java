package com.nexora.messaging.consumer;

import com.nexora.config.RabbitMQConfig;
import com.nexora.messaging.event.OrderCreatedEvent;
import com.nexora.messaging.event.AppointmentCreatedEvent;
import com.nexora.integration.otp.OtpSender;
import com.nexora.integration.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final OtpSender otpSender;
    private final EmailSender emailSender;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(Object event) {
        log.info("[MQ] Received notification → {}", event);
    }
}