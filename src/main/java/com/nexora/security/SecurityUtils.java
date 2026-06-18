package com.nexora.security;

import com.nexora.exception.BusinessException;
import com.nexora.model.entity.Customer;
import com.nexora.model.entity.Order;
import com.nexora.model.entity.User;
import com.nexora.model.enums.StoreRole;
import com.nexora.repository.CustomerRepository;
import com.nexora.repository.OrderRepository;
import com.nexora.repository.StoreMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final StoreMemberRepository storeMemberRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CurrentRequest currentRequest;

    @Transactional(readOnly = true)
    public User getCurrentUser() {

        User user = currentRequest.getUser();

        if (user == null) {
            throw new BusinessException(
                    "User not authenticated",
                    HttpStatus.UNAUTHORIZED
            );
        }

        return user;
    }

    @Transactional(readOnly = true)
    public UUID getCurrentUserId() {
        return getCurrentUser().getId();
    }

    @Transactional(readOnly = true)
    public boolean isStoreMember(UUID storeId) {

        User user = getCurrentUser();

        return storeMemberRepository.existsByUserIdAndStoreId(
                user.getId(),
                storeId
        );
    }

    @Transactional(readOnly = true)
    public boolean isStoreAdmin(UUID storeId) {

        User user = getCurrentUser();

        return storeMemberRepository.existsByUserIdAndStoreIdAndRole(
                user.getId(),
                storeId,
                StoreRole.SUPER_ADMIN
        );
    }

    @Transactional(readOnly = true)
    public boolean canCreateOrder(UUID storeId, UUID customerId) {
        User user = getCurrentUser();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(
                        "Customer not found",
                        HttpStatus.NOT_FOUND
                ));

        if (!customer.getStore().getId().equals(storeId)) {
            return false;
        }

        return isStoreMember(storeId) || isCustomerOwner(user, customer);
    }

    @Transactional(readOnly = true)
    public boolean canAccessOrder(UUID orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        "Order not found",
                        HttpStatus.NOT_FOUND
                ));

        return isStoreMember(order.getStore().getId())
                || isCustomerOwner(user, order.getCustomer());
    }

    @Transactional(readOnly = true)
    public boolean canManageOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        "Order not found",
                        HttpStatus.NOT_FOUND
                ));

        return isStoreAdmin(order.getStore().getId());
    }

    @Transactional(readOnly = true)
    public boolean canAccessCustomerOrders(UUID customerId) {
        User user = getCurrentUser();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(
                        "Customer not found",
                        HttpStatus.NOT_FOUND
                ));

        return isStoreMember(customer.getStore().getId())
                || isCustomerOwner(user, customer);
    }

    private boolean isCustomerOwner(User user, Customer customer) {
        return customer.getUser() != null
                && customer.getUser().getId().equals(user.getId());
    }
}
