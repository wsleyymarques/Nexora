package com.nexora.service;

import com.nexora.dto.request.CreateCustomerRequest;
import com.nexora.dto.request.UpdateCustomerRequest;
import com.nexora.dto.response.CustomerResponse;
import com.nexora.dto.response.UserResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.Customer;
import com.nexora.model.entity.User;
import com.nexora.model.enums.CustomerOrigin;
import com.nexora.repository.CustomerRepository;
import com.nexora.repository.StoreRepository;
import com.nexora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CustomerResponse create(UUID storeId, CreateCustomerRequest request) {

        var store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException(
                        "Store not found", HttpStatus.NOT_FOUND));

        if (customerRepository.findByStoreIdAndPhone(storeId, request.phone()).isPresent()) {
            throw new BusinessException(
                    "Customer with this phone already exists in this store",
                    HttpStatus.CONFLICT);
        }

        var customer = Customer.builder()
                .store(store)
                .name(request.name())
                .phone(request.phone())
                .email(request.email())
                .origin(request.origin() != null
                        ? request.origin()
                        : CustomerOrigin.WPP)
                .build();

        if (request.userId() != null) {
            var user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new BusinessException(
                            "User not found", HttpStatus.NOT_FOUND));
            customer.setUser(user);
        }

        customerRepository.save(customer);
        return toResponse(customer);
    }

    public List<CustomerResponse> findByStore(UUID storeId) {
        return customerRepository.findByStoreId(storeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CustomerResponse findById(UUID storeId, UUID customerId) {
        return toResponse(findCustomerInStore(storeId, customerId));
    }

    @Transactional
    public CustomerResponse update(UUID storeId, UUID customerId,
                                   UpdateCustomerRequest request) {

        var customer = findCustomerInStore(storeId, customerId);

        if (request.phone() != null
                && !request.phone().equals(customer.getPhone())) {

            if (customerRepository.findByStoreIdAndPhone(
                    storeId, request.phone()).isPresent()) {
                throw new BusinessException(
                        "Phone already in use in this store",
                        HttpStatus.CONFLICT);
            }

            customer.setPhone(request.phone());
        }

        if (request.name() != null) customer.setName(request.name());
        if (request.email() != null) customer.setEmail(request.email());

        customerRepository.save(customer);
        return toResponse(customer);
    }

    @Transactional
    public void remove(UUID storeId, UUID customerId) {
        var customer = findCustomerInStore(storeId, customerId);
        customerRepository.delete(customer);
    }

    private Customer findCustomerInStore(UUID storeId, UUID customerId) {
        var customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessException(
                        "Customer not found", HttpStatus.NOT_FOUND));

        if (!customer.getStore().getId().equals(storeId)) {
            throw new BusinessException(
                    "Customer does not belong to this store",
                    HttpStatus.BAD_REQUEST);
        }

        return customer;
    }

    private CustomerResponse toResponse(Customer customer) {
        UserResponse userResponse = null;

        if (customer.getUser() != null) {
            User user = customer.getUser();
            userResponse = new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getPhone(),
                    user.getEmail(),
                    user.getOrigin(),
                    user.getAvatarUrl()
            );
        }

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getOrigin(),
                userResponse
        );
    }
}