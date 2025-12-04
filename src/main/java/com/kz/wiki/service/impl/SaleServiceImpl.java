package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.*;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.*;
import com.kz.wiki.service.SaleService;
import com.kz.wiki.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final DiscountRepository discountRepository;
    private final GiftCardRepository giftCardRepository;
    private final GiftCardUsageRepository giftCardUsageRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_SALE", entityType = "SALE")
    public Sale createSale(Sale sale, String tenantId, Long userId) {
        if (sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new BadRequestException("Sale must have at least one item");
        }

        sale.setTenantId(tenantId);
        sale.setCreatedBy(userId);
        sale.setSaleTime(LocalDateTime.now());
        sale.setType(SaleType.SALE);
        
        // Generate transaction and receipt numbers with retry logic for uniqueness
        if (sale.getTransactionNumber() == null) {
            sale.setTransactionNumber(generateUniqueTransactionNumber(sale.getStoreId(), tenantId));
        }
        if (sale.getReceiptNumber() == null) {
            sale.setReceiptNumber(generateUniqueReceiptNumber(sale.getStoreId(), tenantId));
        }
        
        // Set status
        if (sale.getStatus() == null) {
            sale.setStatus("completed");
        }
        
        if ("completed".equals(sale.getStatus())) {
            sale.setCompletedAt(LocalDateTime.now());
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        // Process each sale item
        for (SaleItem item : sale.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new BadRequestException("Sale item must have a valid product");
            }

            Product product = productRepository.findByIdAndTenantId(
                    item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product", "id", item.getProduct().getId()));

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BadRequestException("Item quantity must be greater than 0");
            }

            // Use wholesale price if specified and useWholesalePrices is true
            BigDecimal unitPrice = item.getPrice() != null ? item.getPrice() : product.getPrice();
            if (sale.getStoreId() != null && item.getWholesalePrice() != null) {
                // Check if wholesale prices should be used (would need a flag in Sale entity)
                // For now, use the provided price
            }

            // Apply item discount
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            if (item.getDiscount() != null && item.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = BigDecimal.ZERO;
                if ("percentage".equals(item.getDiscountType())) {
                    discountAmount = itemTotal.multiply(item.getDiscount()).divide(BigDecimal.valueOf(100));
                } else {
                    discountAmount = item.getDiscount();
                }
                itemTotal = itemTotal.subtract(discountAmount);
            }
            
            item.setPrice(unitPrice);
            // Also set priceColumn to match unit_price (database has both columns)
            item.setPriceColumn(unitPrice);
            item.setTotalPrice(itemTotal);
            item.setSale(sale);
            item.setType("product"); // Default type

            subtotal = subtotal.add(itemTotal);

            // Update stock only if sale is completed
            if ("completed".equals(sale.getStatus())) {
                if (product.getStockQty() < item.getQuantity()) {
                    throw new BadRequestException(
                            String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                                    product.getName(), product.getStockQty(), item.getQuantity()));
                }
                product.setStockQty(product.getStockQty() - item.getQuantity());
                productRepository.save(product);
            }
        }

        sale.setSubtotal(subtotal);
        
        // Apply sale-level discount
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (sale.getDiscount() != null && sale.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
            if ("percentage".equals(sale.getDiscountType())) {
                discountAmount = subtotal.multiply(sale.getDiscount()).divide(BigDecimal.valueOf(100));
            } else {
                discountAmount = sale.getDiscount();
            }
        }
        
        BigDecimal calculatedTotalAmount = subtotal.subtract(discountAmount);
        // Ensure totalAmount is never null (required field)
        if (calculatedTotalAmount == null) {
            calculatedTotalAmount = BigDecimal.ZERO;
        }
        final BigDecimal finalTotalAmount = calculatedTotalAmount;
        sale.setTotalAmount(finalTotalAmount);
        
        // Update customer stats if customer exists
        if (sale.getCustomerId() != null) {
            customerRepository.findByIdAndTenantId(sale.getCustomerId(), tenantId).ifPresent(customer -> {
                BigDecimal currentPurchases = customer.getTotalPurchases() != null ? customer.getTotalPurchases() : BigDecimal.ZERO;
                customer.setTotalPurchases(currentPurchases.add(finalTotalAmount));
                customer.setLastPurchaseDate(LocalDateTime.now());
                if (sale.getLoyaltyPointsEarned() != null) {
                    Integer currentPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
                    customer.setLoyaltyPoints(currentPoints + sale.getLoyaltyPointsEarned());
                }
                customerRepository.save(customer);
            });
        }

        Sale saved = saleRepository.save(sale);
        log.info("Sale created: {} (ID: {}) for tenant: {}", saved.getTransactionNumber(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_SALE", entityType = "SALE")
    public Sale updateSale(Long id, Sale sale, String tenantId) {
        Sale existing = saleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", id));

        if (!"draft".equals(existing.getStatus()) && !"deferred".equals(existing.getStatus())) {
            throw new BadRequestException("Can only update draft or deferred sales");
        }

        // Update fields
        existing.setStoreId(sale.getStoreId());
        existing.setCustomerId(sale.getCustomerId());
        existing.setSellerId(sale.getSellerId());
        existing.setSubtotal(sale.getSubtotal());
        existing.setDiscount(sale.getDiscount());
        existing.setDiscountType(sale.getDiscountType());
        existing.setDiscountCode(sale.getDiscountCode());
        existing.setTotalAmount(sale.getTotalAmount());
        existing.setPaymentMethod(sale.getPaymentMethod());
        existing.setNote(sale.getNote());

        Sale updated = saleRepository.save(existing);
        log.info("Sale updated: {} (ID: {}) for tenant: {}", updated.getTransactionNumber(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "CANCEL_SALE", entityType = "SALE")
    public Sale cancelSale(Long id, String reason, String tenantId, Long userId) {
        Sale sale = saleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", id));

        if ("cancelled".equals(sale.getStatus())) {
            throw new BadRequestException("Sale is already cancelled");
        }

        // Restore stock
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();
            product.setStockQty(product.getStockQty() + item.getQuantity());
            productRepository.save(product);
        }

        sale.setStatus("cancelled");
        sale.setCancelledAt(LocalDateTime.now());
        sale.setCancelReason(reason);

        Sale cancelled = saleRepository.save(sale);
        log.info("Sale cancelled: {} (ID: {}) for tenant: {}", cancelled.getTransactionNumber(), cancelled.getId(), tenantId);
        return cancelled;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_RETURN", entityType = "SALE")
    public Sale createReturn(Long saleId, SaleService.CreateReturnRequest request, String tenantId, Long userId) {
        Sale originalSale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", saleId));

        if (originalSale.getType() == SaleType.RETURN) {
            throw new BadRequestException("Cannot return a return transaction");
        }

        Sale returnSale = new Sale();
        returnSale.setTenantId(tenantId);
        returnSale.setCreatedBy(userId);
        returnSale.setSaleTime(LocalDateTime.now());
        returnSale.setType(SaleType.RETURN);
        returnSale.setStatus("completed");
        returnSale.setCompletedAt(LocalDateTime.now());
        returnSale.setTransactionNumber(generateTransactionNumber(originalSale.getStoreId(), tenantId));
        returnSale.setReceiptNumber(generateReceiptNumber(originalSale.getStoreId(), tenantId));
        returnSale.setStoreId(originalSale.getStoreId());
        returnSale.setCustomerId(originalSale.getCustomerId());
        returnSale.setPaymentMethod(request.getRefundMethod());
        returnSale.setNote(request.getNote());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleService.CreateReturnRequest.ReturnItemRequest returnItem : request.getItems()) {
            SaleItem originalItem = originalSale.getItems().stream()
                    .filter(item -> item.getId().equals(returnItem.getSaleItemId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("SaleItem", "id", returnItem.getSaleItemId()));

            if (returnItem.getQuantity() > originalItem.getQuantity()) {
                throw new BadRequestException("Return quantity cannot exceed original quantity");
            }

            Product product = originalItem.getProduct();
            BigDecimal returnAmount = originalItem.getPrice().multiply(BigDecimal.valueOf(returnItem.getQuantity()));
            totalAmount = totalAmount.add(returnAmount);

            SaleItem returnSaleItem = new SaleItem();
            returnSaleItem.setSale(returnSale);
            returnSaleItem.setProduct(product);
            returnSaleItem.setQuantity(returnItem.getQuantity());
            returnSaleItem.setPrice(originalItem.getPrice());
            returnSaleItem.setTotalPrice(returnAmount.negate());
            returnSaleItem.setType("product");
            returnSale.getItems().add(returnSaleItem);

            // Restore stock
            product.setStockQty(product.getStockQty() + returnItem.getQuantity());
            productRepository.save(product);
        }

        returnSale.setTotalAmount(totalAmount.negate());
        Sale saved = saleRepository.save(returnSale);
        log.info("Return created: {} (ID: {}) for sale {} for tenant: {}", 
                saved.getTransactionNumber(), saved.getId(), saleId, tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_EXCHANGE", entityType = "SALE")
    public Sale createExchange(Long saleId, SaleService.CreateExchangeRequest request, String tenantId, Long userId) {
        Sale originalSale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", saleId));

        Sale exchangeSale = new Sale();
        exchangeSale.setTenantId(tenantId);
        exchangeSale.setCreatedBy(userId);
        exchangeSale.setSaleTime(LocalDateTime.now());
        exchangeSale.setType(SaleType.EXCHANGE);
        exchangeSale.setStatus("completed");
        exchangeSale.setCompletedAt(LocalDateTime.now());
        exchangeSale.setTransactionNumber(generateTransactionNumber(originalSale.getStoreId(), tenantId));
        exchangeSale.setReceiptNumber(generateReceiptNumber(originalSale.getStoreId(), tenantId));
        exchangeSale.setStoreId(originalSale.getStoreId());
        exchangeSale.setCustomerId(originalSale.getCustomerId());
        exchangeSale.setPaymentMethod(request.getPaymentMethod());
        exchangeSale.setNote(request.getNote());

        BigDecimal returnAmount = BigDecimal.ZERO;
        BigDecimal newAmount = BigDecimal.ZERO;

        // Process return items
        for (SaleService.CreateExchangeRequest.ReturnItemRequest returnItem : request.getReturnItems()) {
            SaleItem originalItem = originalSale.getItems().stream()
                    .filter(item -> item.getId().equals(returnItem.getSaleItemId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("SaleItem", "id", returnItem.getSaleItemId()));

            Product product = originalItem.getProduct();
            BigDecimal itemReturnAmount = originalItem.getPrice().multiply(BigDecimal.valueOf(returnItem.getQuantity()));
            returnAmount = returnAmount.add(itemReturnAmount);

            // Restore stock
            product.setStockQty(product.getStockQty() + returnItem.getQuantity());
            productRepository.save(product);
        }

        // Process new items
        for (SaleService.CreateExchangeRequest.NewItemRequest newItem : request.getNewItems()) {
            Product product = productRepository.findByIdAndTenantId(newItem.getProductId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", newItem.getProductId()));

            if (product.getStockQty() < newItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal itemPrice = newItem.getPrice() != null ? newItem.getPrice() : product.getPrice();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(newItem.getQuantity()));
            newAmount = newAmount.add(itemTotal);

            SaleItem exchangeItem = new SaleItem();
            exchangeItem.setSale(exchangeSale);
            exchangeItem.setProduct(product);
            exchangeItem.setQuantity(newItem.getQuantity());
            exchangeItem.setPrice(itemPrice);
            exchangeItem.setTotalPrice(itemTotal);
            exchangeItem.setType("product");
            exchangeSale.getItems().add(exchangeItem);

            // Deduct stock
            product.setStockQty(product.getStockQty() - newItem.getQuantity());
            productRepository.save(product);
        }

        BigDecimal differenceAmount = newAmount.subtract(returnAmount);
        exchangeSale.setTotalAmount(differenceAmount);
        Sale saved = saleRepository.save(exchangeSale);
        log.info("Exchange created: {} (ID: {}) for sale {} for tenant: {}", 
                saved.getTransactionNumber(), saved.getId(), saleId, tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "SAVE_DRAFT", entityType = "SALE")
    public Sale saveDraft(Sale sale, String tenantId, Long userId) {
        if (sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new BadRequestException("Draft must have at least one item");
        }

        sale.setStatus("draft");
        sale.setTenantId(tenantId);
        sale.setCreatedBy(userId);
        sale.setSaleTime(LocalDateTime.now());
        sale.setType(SaleType.SALE);
        
        if (sale.getTransactionNumber() == null) {
            sale.setTransactionNumber(generateUniqueTransactionNumber(sale.getStoreId(), tenantId));
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        // Process each sale item (without updating stock for drafts)
        for (SaleItem item : sale.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new BadRequestException("Sale item must have a valid product");
            }

            Product product = productRepository.findByIdAndTenantId(
                    item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product", "id", item.getProduct().getId()));

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BadRequestException("Item quantity must be greater than 0");
            }

            BigDecimal unitPrice = item.getPrice() != null ? item.getPrice() : product.getPrice();
            
            // Apply item discount
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            if (item.getDiscount() != null && item.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = BigDecimal.ZERO;
                if ("percentage".equals(item.getDiscountType())) {
                    discountAmount = itemTotal.multiply(item.getDiscount()).divide(BigDecimal.valueOf(100));
                } else {
                    discountAmount = item.getDiscount();
                }
                itemTotal = itemTotal.subtract(discountAmount);
            }
            
            item.setPrice(unitPrice);
            // Also set priceColumn to match unit_price (database has both columns)
            item.setPriceColumn(unitPrice);
            item.setTotalPrice(itemTotal);
            item.setSale(sale); // CRITICAL: Set the sale reference
            item.setType("product"); // Default type

            subtotal = subtotal.add(itemTotal);
        }

        sale.setSubtotal(subtotal);
        sale.setTotalAmount(subtotal);

        Sale saved = saleRepository.save(sale);
        log.info("Draft saved: {} (ID: {}) for tenant: {}", saved.getTransactionNumber(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "SAVE_DEFERRED", entityType = "SALE")
    public Sale saveDeferred(Sale sale, String tenantId, Long userId) {
        if (sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new BadRequestException("Deferred sale must have at least one item");
        }

        sale.setStatus("deferred");
        sale.setTenantId(tenantId);
        sale.setCreatedBy(userId);
        sale.setSaleTime(LocalDateTime.now());
        sale.setType(SaleType.SALE);
        
        if (sale.getTransactionNumber() == null) {
            sale.setTransactionNumber(generateUniqueTransactionNumber(sale.getStoreId(), tenantId));
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        // Process each sale item (without updating stock for deferred sales)
        for (SaleItem item : sale.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new BadRequestException("Sale item must have a valid product");
            }

            Product product = productRepository.findByIdAndTenantId(
                    item.getProduct().getId(), tenantId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product", "id", item.getProduct().getId()));

            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BadRequestException("Item quantity must be greater than 0");
            }

            BigDecimal unitPrice = item.getPrice() != null ? item.getPrice() : product.getPrice();
            
            // Apply item discount
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            if (item.getDiscount() != null && item.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discountAmount = BigDecimal.ZERO;
                if ("percentage".equals(item.getDiscountType())) {
                    discountAmount = itemTotal.multiply(item.getDiscount()).divide(BigDecimal.valueOf(100));
                } else {
                    discountAmount = item.getDiscount();
                }
                itemTotal = itemTotal.subtract(discountAmount);
            }
            
            item.setPrice(unitPrice);
            // Also set priceColumn to match unit_price (database has both columns)
            item.setPriceColumn(unitPrice);
            item.setTotalPrice(itemTotal);
            item.setSale(sale); // CRITICAL: Set the sale reference
            item.setType("product"); // Default type

            subtotal = subtotal.add(itemTotal);
        }

        sale.setSubtotal(subtotal);
        sale.setTotalAmount(subtotal);

        Sale saved = saleRepository.save(sale);
        log.info("Deferred sale saved: {} (ID: {}) for tenant: {}", saved.getTransactionNumber(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "COMPLETE_DRAFT", entityType = "SALE")
    public Sale completeDraft(Long id, Sale sale, String tenantId, Long userId) {
        Sale draft = saleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", id));

        if (!"draft".equals(draft.getStatus())) {
            throw new BadRequestException("Sale is not a draft");
        }

        // Update with new data if provided
        if (sale != null) {
            draft.setStoreId(sale.getStoreId());
            draft.setCustomerId(sale.getCustomerId());
            draft.setSellerId(sale.getSellerId());
            draft.setItems(sale.getItems());
            draft.setPaymentMethod(sale.getPaymentMethod());
            draft.setNote(sale.getNote());
        }

        // Complete the sale (update stock, etc.)
        draft.setStatus("completed");
        draft.setCompletedAt(LocalDateTime.now());
        
        // Update stock
        for (SaleItem item : draft.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQty() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQty(product.getStockQty() - item.getQuantity());
            productRepository.save(product);
        }

        Sale completed = saleRepository.save(draft);
        log.info("Draft completed: {} (ID: {}) for tenant: {}", completed.getTransactionNumber(), completed.getId(), tenantId);
        return completed;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "COMPLETE_DEFERRED", entityType = "SALE")
    public Sale completeDeferred(Long id, Sale sale, String tenantId, Long userId) {
        Sale deferred = saleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", id));

        if (!"deferred".equals(deferred.getStatus())) {
            throw new BadRequestException("Sale is not deferred");
        }

        // Similar to completeDraft
        if (sale != null) {
            deferred.setPaymentMethod(sale.getPaymentMethod());
            deferred.setNote(sale.getNote());
        }

        deferred.setStatus("completed");
        deferred.setCompletedAt(LocalDateTime.now());

        // Stock should already be deducted for deferred sales, but verify
        for (SaleItem item : deferred.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQty() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQty(product.getStockQty() - item.getQuantity());
            productRepository.save(product);
        }

        Sale completed = saleRepository.save(deferred);
        log.info("Deferred sale completed: {} (ID: {}) for tenant: {}", completed.getTransactionNumber(), completed.getId(), tenantId);
        return completed;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Sale> findById(Long id, String tenantId) {
        return saleRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findAll(String tenantId) {
        return saleRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findWithFilters(SaleFilters filters, String tenantId) {
        List<Sale> sales = saleRepository.findByTenantId(tenantId);

        // Apply filters
        if (filters.getSearch() != null && !filters.getSearch().trim().isEmpty()) {
            sales = saleRepository.searchByTenantId(tenantId, filters.getSearch().trim());
        }
        if (filters.getStoreId() != null) {
            sales = sales.stream()
                    .filter(s -> s.getStoreId() != null && s.getStoreId().equals(filters.getStoreId()))
                    .collect(Collectors.toList());
        }
        if (filters.getPaymentMethod() != null) {
            sales = sales.stream()
                    .filter(s -> filters.getPaymentMethod().equals(s.getPaymentMethod()))
                    .collect(Collectors.toList());
        }
        if (filters.getSellerId() != null) {
            sales = sales.stream()
                    .filter(s -> s.getSellerId() != null && s.getSellerId().equals(filters.getSellerId()))
                    .collect(Collectors.toList());
        }
        if (filters.getCustomerId() != null) {
            sales = sales.stream()
                    .filter(s -> s.getCustomerId() != null && s.getCustomerId().equals(filters.getCustomerId()))
                    .collect(Collectors.toList());
        }
        if (filters.getMinAmount() != null) {
            sales = sales.stream()
                    .filter(s -> s.getTotalAmount().compareTo(filters.getMinAmount()) >= 0)
                    .collect(Collectors.toList());
        }
        if (filters.getMaxAmount() != null) {
            sales = sales.stream()
                    .filter(s -> s.getTotalAmount().compareTo(filters.getMaxAmount()) <= 0)
                    .collect(Collectors.toList());
        }
        if (filters.getStartDate() != null && filters.getEndDate() != null) {
            sales = saleRepository.findByTenantIdAndDateRangeOnly(tenantId, filters.getStartDate(), filters.getEndDate());
        }
        if (filters.getStatus() != null) {
            sales = sales.stream()
                    .filter(s -> filters.getStatus().equals(s.getStatus()))
                    .collect(Collectors.toList());
        }
        if (filters.getType() != null) {
            SaleType type = SaleType.valueOf(filters.getType().toUpperCase());
            sales = sales.stream()
                    .filter(s -> type.equals(s.getType()))
                    .collect(Collectors.toList());
        }

        return sales;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findDrafts(String tenantId) {
        return saleRepository.findByTenantIdAndStatus(tenantId, "draft");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findDeferred(String tenantId) {
        return saleRepository.findByTenantIdAndStatus(tenantId, "deferred");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findReturns(String tenantId) {
        return saleRepository.findByTenantIdAndType(tenantId, SaleType.RETURN);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findExchanges(String tenantId) {
        return saleRepository.findByTenantIdAndType(tenantId, SaleType.EXCHANGE);
    }

    @Override
    @Transactional(readOnly = true)
    public SaleStatistics getStatistics(SaleStatisticsFilters filters, String tenantId) {
        LocalDate startDate = filters.getStartDate() != null ? filters.getStartDate() : LocalDate.now().minusMonths(1);
        LocalDate endDate = filters.getEndDate() != null ? filters.getEndDate() : LocalDate.now();
        
        List<Sale> sales = saleRepository.findByTenantIdAndDateRangeOnly(tenantId, startDate, endDate);
        
        if (filters.getStoreId() != null) {
            sales = sales.stream()
                    .filter(s -> s.getStoreId() != null && s.getStoreId().equals(filters.getStoreId()))
                    .collect(Collectors.toList());
        }
        if (filters.getSellerId() != null) {
            sales = sales.stream()
                    .filter(s -> s.getSellerId() != null && s.getSellerId().equals(filters.getSellerId()))
                    .collect(Collectors.toList());
        }

        long totalTransactions = sales.stream()
                .filter(s -> s.getType() == SaleType.SALE)
                .count();
        
        BigDecimal totalAmount = sales.stream()
                .filter(s -> s.getType() == SaleType.SALE)
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalReturns = sales.stream()
                .filter(s -> s.getType() == SaleType.RETURN)
                .count();
        
        BigDecimal totalReturnsAmount = sales.stream()
                .filter(s -> s.getType() == SaleType.RETURN)
                .map(Sale::getTotalAmount)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalExchanges = sales.stream()
                .filter(s -> s.getType() == SaleType.EXCHANGE)
                .count();
        
        BigDecimal totalExchangesAmount = sales.stream()
                .filter(s -> s.getType() == SaleType.EXCHANGE)
                .map(Sale::getTotalAmount)
                .map(BigDecimal::abs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Group by payment method
        Map<String, PaymentMethodStatsImpl> paymentMethodMap = new HashMap<>();
        for (Sale sale : sales) {
            if (sale.getPaymentMethod() != null && sale.getType() == SaleType.SALE) {
                paymentMethodMap.computeIfAbsent(sale.getPaymentMethod(), k -> new PaymentMethodStatsImpl(k))
                        .addSale(sale);
            }
        }

        final long finalTotalTransactions = totalTransactions;
        final BigDecimal finalTotalAmount = totalAmount;
        final long finalTotalReturns = totalReturns;
        final BigDecimal finalTotalReturnsAmount = totalReturnsAmount;
        final long finalTotalExchanges = totalExchanges;
        final BigDecimal finalTotalExchangesAmount = totalExchangesAmount;

        return new SaleStatistics() {
            @Override
            public Long getTotalTransactions() { return finalTotalTransactions; }
            @Override
            public BigDecimal getTotalAmount() { return finalTotalAmount; }
            @Override
            public Long getTotalProducts() { return 0L; } // Would need to count from items
            @Override
            public Long getTotalServices() { return 0L; }
            @Override
            public Long getTotalKits() { return 0L; }
            @Override
            public Long getTotalCertificates() { return 0L; }
            @Override
            public Long getTotalReturns() { return finalTotalReturns; }
            @Override
            public BigDecimal getTotalReturnsAmount() { return finalTotalReturnsAmount; }
            @Override
            public Long getTotalExchanges() { return finalTotalExchanges; }
            @Override
            public BigDecimal getTotalExchangesAmount() { return finalTotalExchangesAmount; }
            @Override
            public Long getTotalGiftCardsUsed() { return 0L; }
            @Override
            public BigDecimal getTotalGiftCardsAmount() { return BigDecimal.ZERO; }
            @Override
            public Integer getTotalLoyaltyPointsEarned() { return 0; }
            @Override
            public Integer getTotalLoyaltyPointsUsed() { return 0; }
            @Override
            public BigDecimal getTotalDebtPayments() { return BigDecimal.ZERO; }
            @Override
            public CustomerBalance getCustomerBalance() {
                return new CustomerBalance() {
                    @Override
                    public BigDecimal getTotalAccrued() { return finalTotalAmount; }
                    @Override
                    public BigDecimal getTotalSpent() { return BigDecimal.ZERO; }
                    @Override
                    public BigDecimal getCurrentBalance() { return finalTotalAmount; }
                };
            }
            @Override
            public List<PaymentMethodStats> getByPaymentMethod() {
                return new ArrayList<>(paymentMethodMap.values());
            }
            @Override
            public List<TypeStats> getByType() { return new ArrayList<>(); }
        };
    }

    @Override
    @Transactional(readOnly = true)
    public SaleStatisticsByDate getStatisticsByDate(LocalDate startDate, LocalDate endDate, String groupBy, String tenantId) {
        List<Sale> sales = saleRepository.findByTenantIdAndDateRangeOnly(tenantId, startDate, endDate);
        
        Map<String, DateStatsImpl> dateMap = new HashMap<>();
        for (Sale sale : sales) {
            String dateKey = sale.getSaleTime().toLocalDate().toString();
            dateMap.computeIfAbsent(dateKey, k -> new DateStatsImpl(k))
                    .addSale(sale);
        }

        List<DateStats> stats = new ArrayList<>(dateMap.values());
        stats.sort(Comparator.comparing(DateStats::getDate));

        return () -> stats;
    }

    @Override
    @Transactional(readOnly = true)
    public String getNextTransactionNumber(Long storeId, String tenantId) {
        return generateTransactionNumber(storeId, tenantId);
    }

    private String generateUniqueTransactionNumber(Long storeId, String tenantId) {
        String transactionNumber;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            String year = String.valueOf(LocalDate.now().getYear());
            // Use timestamp + random to ensure uniqueness
            long timestamp = System.currentTimeMillis() % 100000; // Last 5 digits of timestamp
            long count = saleRepository.countByTenantId(tenantId) + 1;
            // Add tenant hash to make it unique across tenants
            String tenantHash = tenantId.length() > 4 ? tenantId.substring(0, 4) : tenantId;
            transactionNumber = String.format("TXN-%s-%s-%05d-%05d", year, tenantHash.toUpperCase(), count, timestamp);
            attempts++;
        } while (saleRepository.findByTransactionNumberAndTenantId(transactionNumber, tenantId).isPresent() && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            // Fallback: use UUID-based number
            String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            transactionNumber = String.format("TXN-%s-%s", LocalDate.now().getYear(), uuid);
        }
        
        return transactionNumber;
    }

    private String generateUniqueReceiptNumber(Long storeId, String tenantId) {
        String receiptNumber;
        int attempts = 0;
        int maxAttempts = 10;
        
        do {
            String year = String.valueOf(LocalDate.now().getYear());
            // Use timestamp + random to ensure uniqueness
            long timestamp = System.currentTimeMillis() % 100000; // Last 5 digits of timestamp
            long count = saleRepository.countByTenantId(tenantId) + 1;
            // Add tenant hash to make it unique across tenants
            String tenantHash = tenantId.length() > 4 ? tenantId.substring(0, 4) : tenantId;
            receiptNumber = String.format("RCP-%s-%s-%05d-%05d", year, tenantHash.toUpperCase(), count, timestamp);
            attempts++;
        } while (saleRepository.findByReceiptNumberAndTenantId(receiptNumber, tenantId).isPresent() && attempts < maxAttempts);
        
        if (attempts >= maxAttempts) {
            // Fallback: use UUID-based number
            String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            receiptNumber = String.format("RCP-%s-%s", LocalDate.now().getYear(), uuid);
        }
        
        return receiptNumber;
    }
    
    private String generateTransactionNumber(Long storeId, String tenantId) {
        return generateUniqueTransactionNumber(storeId, tenantId);
    }

    private String generateReceiptNumber(Long storeId, String tenantId) {
        return generateUniqueReceiptNumber(storeId, tenantId);
    }

    private BigDecimal calculateSubtotal(Sale sale) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (SaleItem item : sale.getItems()) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                if (item.getDiscount() != null && item.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal discountAmount = BigDecimal.ZERO;
                    if ("percentage".equals(item.getDiscountType())) {
                        discountAmount = itemTotal.multiply(item.getDiscount()).divide(BigDecimal.valueOf(100));
                    } else {
                        discountAmount = item.getDiscount();
                    }
                    itemTotal = itemTotal.subtract(discountAmount);
                }
                item.setTotalPrice(itemTotal);
                subtotal = subtotal.add(itemTotal);
            }
        }
        return subtotal;
    }

    // Helper classes for statistics
    private static class PaymentMethodStatsImpl implements PaymentMethodStats {
        private final String method;
        private long count = 0;
        private BigDecimal amount = BigDecimal.ZERO;

        PaymentMethodStatsImpl(String method) {
            this.method = method;
        }

        void addSale(Sale sale) {
            count++;
            amount = amount.add(sale.getTotalAmount());
        }

        @Override
        public String getMethod() { return method; }
        @Override
        public Long getCount() { return count; }
        @Override
        public BigDecimal getAmount() { return amount; }
    }

    private static class DateStatsImpl implements DateStats {
        private final String date;
        private long transactions = 0;
        private BigDecimal amount = BigDecimal.ZERO;
        private long products = 0;
        private long services = 0;

        DateStatsImpl(String date) {
            this.date = date;
        }

        void addSale(Sale sale) {
            transactions++;
            amount = amount.add(sale.getTotalAmount());
            products += sale.getItems().size(); // Simplified
        }

        @Override
        public String getDate() { return date; }
        @Override
        public Long getTransactions() { return transactions; }
        @Override
        public BigDecimal getAmount() { return amount; }
        @Override
        public Long getProducts() { return products; }
        @Override
        public Long getServices() { return services; }
    }
}
