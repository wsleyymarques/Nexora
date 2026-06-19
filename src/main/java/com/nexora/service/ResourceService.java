package com.nexora.service;

import com.nexora.dto.request.CreateResourceRequest;
import com.nexora.dto.request.UpdateResourceRequest;
import com.nexora.dto.response.ResourceResponse;
import com.nexora.exception.BusinessException;
import com.nexora.model.entity.Resource;
import com.nexora.model.entity.Store;
import com.nexora.model.entity.StoreMember;
import com.nexora.model.enums.ResourceType;
import com.nexora.repository.ResourceRepository;
import com.nexora.repository.StoreMemberRepository;
import com.nexora.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final StoreRepository storeRepository;
    private final StoreMemberRepository storeMemberRepository;

    public ResourceResponse create(
            UUID storeId,
            CreateResourceRequest request
    ) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Store not found",
                                HttpStatus.NOT_FOUND
                        )
                );

        StoreMember storeMember = null;

        if (request.type() == ResourceType.PERSON) {

            if (request.storeMemberId() == null) {
                throw new BusinessException(
                        "Store member is required for PERSON resource",
                        HttpStatus.BAD_REQUEST
                );
            }

            storeMember = storeMemberRepository
                    .findById(request.storeMemberId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Store member not found",
                                    HttpStatus.NOT_FOUND
                            )
                    );

            if (!storeMember.getStore().getId().equals(storeId)) {
                throw new BusinessException(
                        "Store member does not belong to store",
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        Resource resource = Resource.builder()
                .store(store)
                .storeMember(storeMember)
                .name(request.name())
                .type(request.type())
                .active(true)
                .build();

        resourceRepository.save(resource);

        return toResponse(resource);
    }

    @Transactional(readOnly = true)
    public List<ResourceResponse> findByStore(UUID storeId) {

        return resourceRepository
                .findByStoreIdAndActiveTrue(storeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResourceResponse findById(
            UUID storeId,
            UUID resourceId
    ) {

        Resource resource = resourceRepository
                .findByIdAndStoreId(resourceId, storeId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Resource not found",
                                HttpStatus.NOT_FOUND
                        )
                );

        return toResponse(resource);
    }

    public ResourceResponse update(
            UUID storeId,
            UUID resourceId,
            UpdateResourceRequest request
    ) {

        Resource resource = resourceRepository
                .findByIdAndStoreId(resourceId, storeId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Resource not found",
                                HttpStatus.NOT_FOUND
                        )
                );

        if (request.name() != null) {
            resource.setName(request.name());
        }

        if (resource.getType() == ResourceType.PERSON
                && request.storeMemberId() != null) {

            StoreMember storeMember = storeMemberRepository
                    .findById(request.storeMemberId())
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Store member not found",
                                    HttpStatus.NOT_FOUND
                            )
                    );

            if (!storeMember.getStore().getId().equals(storeId)) {
                throw new BusinessException(
                        "Store member does not belong to store",
                        HttpStatus.BAD_REQUEST
                );
            }

            resource.setStoreMember(storeMember);
        }

        return toResponse(resourceRepository.save(resource));
    }

    public void deactivate(
            UUID storeId,
            UUID resourceId
    ) {

        Resource resource = resourceRepository
                .findByIdAndStoreId(resourceId, storeId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Resource not found",
                                HttpStatus.NOT_FOUND
                        )
                );

        resource.setActive(false);

        resourceRepository.save(resource);
    }

    private ResourceResponse toResponse(Resource resource) {

        return new ResourceResponse(
                resource.getId(),
                resource.getName(),
                resource.getType(),
                resource.isActive(),
                resource.getStoreMember() != null
                        ? resource.getStoreMember().getId()
                        : null
        );
    }
}