package com.kz.wiki.controller;

import com.kz.wiki.dto.request.CreateCustomerRequest;
import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.entity.Customer;
import com.kz.wiki.entity.Sale;
import com.kz.wiki.service.CustomerService;
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
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setCardNumber(request.getCardNumber());
        customer.setNotes(request.getNotes());
        
        Customer created = customerService.create(customer, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean hasDebt,
            @RequestParam(required = false) Boolean hasLoyaltyPoints) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        List<Customer> customers;
        if (search != null && !search.trim().isEmpty()) {
            customers = customerService.search(search.trim(), tenantId);
        } else if (hasDebt != null && hasDebt) {
            customers = customerService.findWithDebt(tenantId);
        } else if (hasLoyaltyPoints != null && hasLoyaltyPoints) {
            customers = customerService.findWithLoyaltyPoints(tenantId);
        } else {
            customers = customerService.findAll(tenantId);
        }
        
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'SELLER')")
    public ResponseEntity<ApiResponse<List<Customer>>> quickSearch(
            @RequestParam(required = true) String q,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Customer> customers = customerService.quickSearch(q, tenantId, Math.min(limit, 20));
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        return customerService.findById(id, tenantId)
                .map(customer -> ResponseEntity.ok(ApiResponse.success(customer)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Customer not found")));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CreateCustomerRequest request) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setCardNumber(request.getCardNumber());
        customer.setNotes(request.getNotes());
        
        Customer updated = customerService.update(id, customer, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        customerService.delete(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }

    @GetMapping("/{id}/purchases")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Sale>>> getPurchaseHistory(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        List<Sale> purchases = customerService.getPurchaseHistory(id, tenantId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(purchases));
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CustomerService.CustomerBalance>> getBalance(@PathVariable Long id) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        CustomerService.CustomerBalance balance = customerService.getBalance(id, tenantId);
        return ResponseEntity.ok(ApiResponse.success(balance));
    }
}

