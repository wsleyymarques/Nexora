package com.nexora.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // exchanges
    public static final String ORDERS_EXCHANGE = "nexora.orders";
    public static final String APPOINTMENTS_EXCHANGE = "nexora.appointments";
    public static final String NOTIFICATIONS_EXCHANGE = "nexora.notifications";

    // queues
    public static final String ORDER_CREATED_QUEUE = "nexora.orders.created";
    public static final String ORDER_STATUS_QUEUE = "nexora.orders.status";
    public static final String APPOINTMENT_CREATED_QUEUE = "nexora.appointments.created";
    public static final String APPOINTMENT_STATUS_QUEUE = "nexora.appointments.status";
    public static final String NOTIFICATION_QUEUE = "nexora.notifications";

    // dead letter queues
    public static final String ORDER_CREATED_DLQ = "nexora.orders.created.dlq";
    public static final String APPOINTMENT_CREATED_DLQ = "nexora.appointments.created.dlq";

    // routing keys
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_STATUS_KEY = "order.status";
    public static final String APPOINTMENT_CREATED_KEY = "appointment.created";
    public static final String APPOINTMENT_STATUS_KEY = "appointment.status";
    public static final String NOTIFICATION_KEY = "notification";

    // exchanges
    @Bean
    public TopicExchange ordersExchange() {
        return new TopicExchange(ORDERS_EXCHANGE);
    }

    @Bean
    public TopicExchange appointmentsExchange() {
        return new TopicExchange(APPOINTMENTS_EXCHANGE);
    }

    @Bean
    public TopicExchange notificationsExchange() {
        return new TopicExchange(NOTIFICATIONS_EXCHANGE);
    }

    // queues com dead letter
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", ORDER_CREATED_DLQ)
                .build();
    }

    @Bean
    public Queue orderCreatedDlq() {
        return QueueBuilder.durable(ORDER_CREATED_DLQ).build();
    }

    @Bean
    public Queue orderStatusQueue() {
        return QueueBuilder.durable(ORDER_STATUS_QUEUE).build();
    }

    @Bean
    public Queue appointmentCreatedQueue() {
        return QueueBuilder.durable(APPOINTMENT_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", APPOINTMENT_CREATED_DLQ)
                .build();
    }

    @Bean
    public Queue appointmentCreatedDlq() {
        return QueueBuilder.durable(APPOINTMENT_CREATED_DLQ).build();
    }

    @Bean
    public Queue appointmentStatusQueue() {
        return QueueBuilder.durable(APPOINTMENT_STATUS_QUEUE).build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }

    // bindings
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
                .to(ordersExchange())
                .with(ORDER_CREATED_KEY);
    }

    @Bean
    public Binding orderStatusBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(ordersExchange())
                .with(ORDER_STATUS_KEY);
    }

    @Bean
    public Binding appointmentCreatedBinding() {
        return BindingBuilder.bind(appointmentCreatedQueue())
                .to(appointmentsExchange())
                .with(APPOINTMENT_CREATED_KEY);
    }

    @Bean
    public Binding appointmentStatusBinding() {
        return BindingBuilder.bind(appointmentStatusQueue())
                .to(appointmentsExchange())
                .with(APPOINTMENT_STATUS_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationsExchange())
                .with(NOTIFICATION_KEY);
    }

    // serialização JSON
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}