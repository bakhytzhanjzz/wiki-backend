package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateWriteOffRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.WriteOff;
import com.kz.wiki.entity.WriteOffItem;
import com.kz.wiki.entity.Product;
import com.kz.wiki.service.WriteOffService;
import com.kz.wiki.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/write-offs")
@RequiredArgsConstructor
public class WriteOffController {

    private final WriteOffService writeOffService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<WriteOff>> createWriteOff(@Valid @RequestBody CreateWriteOffRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long userId = SecurityUtil.getCurrentUserId();

        WriteOff writeOff = new WriteOff();
        writeOff.setName(request.getName());
        writeOff.setStoreId(request.getStoreId());
        writeOff.setType(request.getType());
        writeOff.setReason(request.getReason());

        for (CreateWriteOffRequest.WriteOffItemRequest itemRequest : request.getItems()) {
            WriteOffItem item = new WriteOffItem();
            Product product = new Product();
            product.setId(itemRequest.getProductId());
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            writeOff.getItems().add(item);
        }

        WriteOff created = writeOffService.create(writeOff, tenantId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Write-off created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<WriteOff>>> getAllWriteOffs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long userId) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        List<WriteOff> writeOffs;
        if (search != null && !search.trim().isEmpty()) {
            writeOffs = writeOffService.search(search.trim(), tenantId);
        } else if (storeId != null) {
            writeOffs = writeOffService.findByStoreId(storeId, tenantId);
        } else if (type != null && !type.trim().isEmpty()) {
            writeOffs = writeOffService.findByType(type.trim(), tenantId);
        } else if (startDate != null && endDate != null) {
            writeOffs = writeOffService.findByDateRange(startDate, endDate, tenantId);
        } else {
            writeOffs = writeOffService.findAll(tenantId);
        }

        return ResponseEntity.ok(ApiResponse.success(writeOffs));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<WriteOff>> getWriteOff(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return writeOffService.findById(id, tenantId)
                .map(writeOff -> ResponseEntity.ok(ApiResponse.success(writeOff)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Write-off not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<WriteOff>> updateWriteOff(
            @PathVariable Long id,
            @Valid @RequestBody CreateWriteOffRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();

        WriteOff writeOff = new WriteOff();
        writeOff.setName(request.getName());
        writeOff.setStoreId(request.getStoreId());
        writeOff.setType(request.getType());
        writeOff.setReason(request.getReason());

        WriteOff updated = writeOffService.update(id, writeOff, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Write-off updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteWriteOff(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        writeOffService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Write-off deleted successfully", null));
    }
}



