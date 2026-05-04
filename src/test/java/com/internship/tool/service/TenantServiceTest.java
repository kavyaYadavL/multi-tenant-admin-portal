package com.internship.tool.service;

import com.internship.tool.dto.TenantDTO;
import com.internship.tool.entity.Tenant;
import com.internship.tool.exception.BadRequestException;
import com.internship.tool.exception.NotFoundException;
import com.internship.tool.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    private Tenant tenant;
    private TenantDTO tenantDTO;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder()
                .id(1L)
                .name("Test Tenant")
                .description("Test Description")
                .status("ACTIVE")
                .build();

        tenantDTO = TenantDTO.builder()
                .id(1L)
                .name("Test Tenant")
                .description("Test Description")
                .status("ACTIVE")
                .build();
    }

    // --- createTenant Tests ---

    @Test
    void createTenant_Success() {
        when(tenantRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tenantRepository.save(any(Tenant.class))).thenReturn(tenant);

        TenantDTO created = tenantService.createTenant(tenantDTO);

        assertNotNull(created);
        assertEquals("Test Tenant", created.getName());
        verify(tenantRepository, times(1)).save(any(Tenant.class));
    }

    @Test
    void createTenant_ThrowsBadRequestException_WhenNameExists() {
        when(tenantRepository.findByName(anyString())).thenReturn(Optional.of(tenant));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            tenantService.createTenant(tenantDTO);
        });

        assertEquals("Tenant with name Test Tenant already exists", exception.getMessage());
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    // --- getAllTenants Tests ---

    @Test
    void getAllTenants_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tenant> page = new PageImpl<>(List.of(tenant));
        when(tenantRepository.findAll(pageable)).thenReturn(page);

        Page<TenantDTO> result = tenantService.getAllTenants(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Tenant", result.getContent().get(0).getName());
    }

    @Test
    void getAllTenants_ReturnsEmptyPage_WhenNoTenants() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tenant> page = Page.empty();
        when(tenantRepository.findAll(pageable)).thenReturn(page);

        Page<TenantDTO> result = tenantService.getAllTenants(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getTenantById Tests ---

    @Test
    void getTenantById_Success() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));

        TenantDTO found = tenantService.getTenantById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("Test Tenant", found.getName());
    }

    @Test
    void getTenantById_ThrowsNotFoundException_WhenIdDoesNotExist() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tenantService.getTenantById(1L);
        });

        assertEquals("Tenant not found with id: 1", exception.getMessage());
    }

    // --- updateTenant Tests ---

    @Test
    void updateTenant_Success() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        
        Tenant updatedTenant = Tenant.builder()
                .id(1L)
                .name("Updated Tenant")
                .description("Updated Description")
                .status("INACTIVE")
                .build();
                
        when(tenantRepository.save(any(Tenant.class))).thenReturn(updatedTenant);

        tenantDTO.setName("Updated Tenant");
        tenantDTO.setDescription("Updated Description");
        tenantDTO.setStatus("INACTIVE");

        TenantDTO result = tenantService.updateTenant(1L, tenantDTO);

        assertNotNull(result);
        assertEquals("Updated Tenant", result.getName());
        assertEquals("INACTIVE", result.getStatus());
    }

    @Test
    void updateTenant_ThrowsNotFoundException_WhenIdDoesNotExist() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tenantService.updateTenant(1L, tenantDTO);
        });

        assertEquals("Tenant not found with id: 1", exception.getMessage());
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    // --- deleteTenant Tests ---

    @Test
    void deleteTenant_Success() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        doNothing().when(tenantRepository).delete(tenant);

        assertDoesNotThrow(() -> {
            tenantService.deleteTenant(1L);
        });

        verify(tenantRepository, times(1)).delete(tenant);
    }

    @Test
    void deleteTenant_ThrowsNotFoundException_WhenIdDoesNotExist() {
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            tenantService.deleteTenant(1L);
        });

        assertEquals("Tenant not found with id: 1", exception.getMessage());
        verify(tenantRepository, never()).delete(any(Tenant.class));
    }
}
