package com.nexora.service;

import com.nexora.dto.request.CreateOrderRequest;
import com.nexora.dto.request.OrderItemRequest;
import com.nexora.dto.request.UpdateOrderStatusRequest;
import com.nexora.dto.response.AppointmentResponse;
import com.nexora.dto.response.OrderItemResponse;
import com.nexora.dto.response.OrderResponse;
import com.nexora.exception.BusinessException;
import com.nexora.messaging.event.OrderCreatedEvent;
import com.nexora.messaging.event.OrderStatusChangedEvent;
import com.nexora.messaging.producer.MessageProducer;
import com.nexora.model.entity.*;
import com.nexora.model.enums.OrderStatus;
import com.nexora.model.enums.ProductType;
import com.nexora.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AppointmentRepository appointmentRepository;
    private final StoreRepository storeRepository;
    private final AvailabilityService availabilityService;
    private final MessageProducer messageProducer;

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new BusinessException("Store not found", HttpStatus.NOT_FOUND));

        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new BusinessException("Customer not found", HttpStatus.NOT_FOUND));

        Order order = Order.builder()
                .store(store)
                .customer(customer)
                .channel(request.channel())
                .status(OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new BusinessException(
                            "Product not found: " + itemRequest.productId(), HttpStatus.NOT_FOUND));

            if (!product.isActive()) {
                throw new BusinessException(
                        "Product is inactive: " + product.getName(), HttpStatus.BAD_REQUEST);
            }

            BigDecimal itemTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .estimatedMinutes(itemRequest.estimatedMinutes())
                    .build();

            if (product.getScheduleConfig() != null && itemRequest.scheduledAt() != null) {
                item = createWithAppointment(item, product, customer, store, itemRequest);
            }

            order.getItems().add(item);
            total = total.add(itemTotal);
        }

        order.setTotal(total);
        orderRepository.save(order);

        publishOrderCreated(order, customer);

        return toResponse(order);
    }

    private OrderItem createWithAppointment(OrderItem item, Product product,
                                            Customer customer, Store store,
                                            OrderItemRequest itemRequest) {

        ScheduleConfig config = product.getScheduleConfig();
        int duration = config.getDurationMinutes();

        Resource resource = availabilityService.findAvailableResource(
                store.getId(),
                config.getResourceType(),
                itemRequest.scheduledAt(),
                duration
        );

        item.setEstimatedMinutes(duration);

        Appointment appointment = Appointment.builder()
                .orderItem(item)
                .customer(customer)
                .resource(resource)
                .scheduledAt(itemRequest.scheduledAt())
                .durationMinutes(duration)
                .build();

        item.setAppointment(appointment);
        return item;
    }

    private void publishOrderCreated(Order order, Customer customer) {
        messageProducer.publishOrderCreated(OrderCreatedEvent.builder()
                .orderId(order.getId())
                .storeId(order.getStore().getId())
                .customerId(customer.getId())
                .customerName(customer.getName())
                .customerPhone(customer.getPhone())
                .channel(order.getChannel())
                .status(order.getStatus())
                .total(order.getTotal())
                .createdAt(order.getCreatedAt())
                .build());
    }

    public OrderResponse findById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
        return toResponse(order);
    }

    public List<OrderResponse> findByStore(UUID storeId) {
        return orderRepository.findByStoreIdOrderByCreatedAtDesc(storeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> findByCustomer(UUID customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStore().getId(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getChannel(),
                order.getStatus(),
                order.getTotal(),
                items,
                order.getCreatedAt()
        );
    }

    @Transactional
    public OrderResponse updateStatus(UUID orderId, UpdateOrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.status());
        orderRepository.save(order);

        messageProducer.publishOrderStatusChanged(OrderStatusChangedEvent.builder()
                .orderId(order.getId())
                .customerId(order.getCustomer().getId())
                .customerPhone(order.getCustomer().getPhone())
                .customerEmail(order.getCustomer().getEmail())
                .oldStatus(oldStatus)
                .newStatus(request.status())
                .build());

        return toResponse(order);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        AppointmentResponse appointmentResponse = null;

        if (item.getAppointment() != null) {
            Appointment a = item.getAppointment();
            appointmentResponse = new AppointmentResponse(
                    a.getId(),
                    a.getResource().getId(),
                    a.getResource().getName(),
                    a.getScheduledAt(),
                    a.getDurationMinutes(),
                    a.getStatus()
            );
        }

        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getEstimatedMinutes(),
                appointmentResponse
        );
    }
}