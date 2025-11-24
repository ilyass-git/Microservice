package emsi.ma.contratservice.controller;

import emsi.ma.contratservice.domain.dto.TenantDto;
import emsi.ma.contratservice.domain.entity.Tenant;
import emsi.ma.contratservice.mapper.TenantMapper;
import emsi.ma.contratservice.service.ITenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final ITenantService tenantService;
    private final TenantMapper tenantMapper;

    @PostMapping
    public ResponseEntity<TenantDto> create(@RequestBody TenantDto tenantDto) {
        Tenant tenant = tenantMapper.toEntity(tenantDto);
        Tenant created = tenantService.create(tenant);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantDto> getById(@PathVariable Long id) {
        return tenantService.getById(id)
                .map(tenant -> ResponseEntity.ok(tenantMapper.toDto(tenant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TenantDto>> getAll() {
        List<TenantDto> tenants = tenantService.getAll().stream()
                .map(tenantMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/contract/{contractId}")
    public ResponseEntity<List<TenantDto>> getByContractId(@PathVariable Long contractId) {
        List<TenantDto> tenants = tenantService.getByContractId(contractId).stream()
                .map(tenantMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TenantDto>> getByUserId(@PathVariable Long userId) {
        List<TenantDto> tenants = tenantService.getByUserId(userId).stream()
                .map(tenantMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDto> update(@PathVariable Long id, @RequestBody TenantDto tenantDto) {
        Tenant tenant = tenantMapper.toEntity(tenantDto);
        Tenant updated = tenantService.update(id, tenant);
        return ResponseEntity.ok(tenantMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

