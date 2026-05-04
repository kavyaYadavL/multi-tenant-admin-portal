package com.internship.tool.service;

import com.internship.tool.dto.TenantDTO;
import com.internship.tool.entity.Tenant;
import com.internship.tool.exception.BadRequestException;
import com.internship.tool.exception.NotFoundException;
import com.internship.tool.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantDTO createTenant(TenantDTO tenantDTO) {
        if (tenantRepository.findByName(tenantDTO.getName()).isPresent()) {
            throw new BadRequestException("Tenant with name " + tenantDTO.getName() + " already exists");
        }

        Tenant tenant = Tenant.builder()
                .name(tenantDTO.getName())
                .description(tenantDTO.getDescription())
                .status(tenantDTO.getStatus())
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);
        return mapToDTO(savedTenant);
    }

    public Page<TenantDTO> getAllTenants(Pageable pageable) {
        return tenantRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Cacheable(value = "tenants", key = "#id")
    public TenantDTO getTenantById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));
        return mapToDTO(tenant);
    }

    @Transactional
    @CachePut(value = "tenants", key = "#id")
    public TenantDTO updateTenant(Long id, TenantDTO tenantDTO) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));

        tenant.setName(tenantDTO.getName());
        tenant.setDescription(tenantDTO.getDescription());
        tenant.setStatus(tenantDTO.getStatus());

        Tenant updatedTenant = tenantRepository.save(tenant);
        return mapToDTO(updatedTenant);
    }

    @Transactional
    @CacheEvict(value = "tenants", key = "#id")
    public void deleteTenant(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tenant not found with id: " + id));
        tenantRepository.delete(tenant);
    }


    private TenantDTO mapToDTO(Tenant tenant) {
        return TenantDTO.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .description(tenant.getDescription())
                .status(tenant.getStatus())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }
}
