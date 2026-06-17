package com.nexora.service;

import com.nexora.dto.request.ProductCreateRequest;
import com.nexora.dto.request.ProductUpdateRequest;
import com.nexora.dto.response.ProductResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.Product;
import com.nexora.model.entity.Store;
import com.nexora.model.entity.User;
import com.nexora.model.enums.StoreRole;
import com.nexora.repository.ProductRepository;
import com.nexora.repository.StoreMemberRepository;
import com.nexora.repository.StoreRepository;
import com.nexora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final StoreMemberRepository storeMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProductResponse create(UUID storeId, ProductCreateRequest request) {
        Store store = getStoreOrThrow(storeId);
        ensureAdmin(storeId);

        var product = Product.builder()
                .store(store)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .type(request.type())
                .build();

        productRepository.save(product);
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list(UUID storeId, Boolean active) {
        getStoreOrThrow(storeId);
        ensureMember(storeId);

        List<Product> products = (active == null)
                ? productRepository.findByStoreId(storeId)
                : productRepository.findByStoreIdAndActive(storeId, active);

        return products.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(UUID storeId, UUID productId) {
        getStoreOrThrow(storeId);
        ensureMember(storeId);

        return productRepository.findByIdAndStoreId(productId, storeId)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public ProductResponse update(UUID storeId, UUID productId, ProductUpdateRequest request) {
        getStoreOrThrow(storeId);
        ensureAdmin(storeId);

        var product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setType(request.type());

        return toResponse(product);
    }

    @Transactional
    public void delete(UUID storeId, UUID productId) {
        getStoreOrThrow(storeId);
        ensureAdmin(storeId);

        var product = productRepository.findByIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));

        product.setActive(false);
    }

    private Store getStoreOrThrow(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new BusinessException("Store not found", HttpStatus.NOT_FOUND));
    }

    private void ensureMember(UUID storeId) {
        var user = currentUser();
        if (storeMemberRepository.findByUserIdAndStoreId(user.getId(), storeId).isEmpty()) {
            throw new BusinessException("You do not have access to this store", HttpStatus.FORBIDDEN);
        }
    }

    private void ensureAdmin(UUID storeId) {
        var user = currentUser();
        var membership = storeMemberRepository.findByUserIdAndStoreId(user.getId(), storeId)
                .orElseThrow(() -> new BusinessException("You do not have access to this store", HttpStatus.FORBIDDEN));

        if (membership.getRole() != StoreRole.SUPER_ADMIN) {
            throw new BusinessException("Only SUPER_ADMIN can manage products", HttpStatus.FORBIDDEN);
        }
    }

    private User currentUser() {
        var email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Authenticated user not found", HttpStatus.UNAUTHORIZED));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getStore().getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getType(),
                product.isActive()
        );
    }
}
