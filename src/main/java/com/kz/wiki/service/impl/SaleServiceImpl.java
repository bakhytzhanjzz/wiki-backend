package com.kz.wiki.service.impl;

import com.kz.wiki.entity.Product;
import com.kz.wiki.entity.Sale;
import com.kz.wiki.entity.SaleItem;
import com.kz.wiki.entity.SaleType;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ProductRepository;
import com.kz.wiki.repository.SaleRepository;
import com.kz.wiki.service.SaleService;
import com.kz.wiki.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Sale createSale(Sale sale, String tenantId, Long userId) {
        if (sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new BadRequestException("Sale must have at least one item");
        }

        sale.setTenantId(tenantId);
        sale.setCreatedBy(userId);
        sale.setSaleTime(LocalDateTime.now());
        sale.setType(SaleType.SALE);

        BigDecimal totalAmount = BigDecimal.ZERO;

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

            // Check stock availability
            if (product.getStockQty() < item.getQuantity()) {
                throw new BadRequestException(
                        String.format("Insufficient stock for product '%s'. Available: %d, Requested: %d",
                                product.getName(), product.getStockQty(), item.getQuantity()));
            }

            // Set unit price from product
            BigDecimal unitPrice = product.getPrice();
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setSale(sale);

            totalAmount = totalAmount.add(item.getTotalPrice());

            // Update product stock
            product.setStockQty(product.getStockQty() - item.getQuantity());
            productRepository.save(product);
        }

        sale.setTotalAmount(totalAmount);
        Sale saved = saleRepository.save(sale);

        log.info("Sale created: ID {} with {} items, total: {} for tenant: {}", 
                saved.getId(), saved.getItems().size(), totalAmount, tenantId);
        return saved;
    }

    @Override
    @Transactional
    public Sale createReturn(Long saleId, String tenantId, Long userId) {
        Sale originalSale = saleRepository.findByIdAndTenantId(saleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Sale", "id", saleId));

        if (originalSale.getType() == SaleType.RETURN) {
            throw new BadRequestException("Cannot return a return transaction");
        }

        // Create return sale
        Sale returnSale = new Sale();
        returnSale.setTenantId(tenantId);
        returnSale.setCreatedBy(userId);
        returnSale.setSaleTime(LocalDateTime.now());
        returnSale.setType(SaleType.RETURN);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process return items (reverse the original sale)
        for (SaleItem originalItem : originalSale.getItems()) {
            Product product = originalItem.getProduct();

            // Create return item
            SaleItem returnItem = new SaleItem();
            returnItem.setSale(returnSale);
            returnItem.setProduct(product);
            returnItem.setQuantity(originalItem.getQuantity());
            returnItem.setUnitPrice(originalItem.getUnitPrice());
            returnItem.setTotalPrice(originalItem.getTotalPrice().negate()); // Negative for return

            totalAmount = totalAmount.add(returnItem.getTotalPrice());

            // Restore product stock
            product.setStockQty(product.getStockQty() + originalItem.getQuantity());
            productRepository.save(product);
        }

        returnSale.setTotalAmount(totalAmount);
        Sale saved = saleRepository.save(returnSale);

        log.info("Return created: ID {} for sale ID {}, total: {} for tenant: {}", 
                saved.getId(), saleId, totalAmount, tenantId);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public java.util.Optional<Sale> findById(Long id, String tenantId) {
        return saleRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findAll(String tenantId) {
        return saleRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> findByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        LocalDateTime start = DateUtil.getStartOfDay(startDate);
        LocalDateTime end = DateUtil.getEndOfDay(endDate);
        return saleRepository.findByTenantIdAndDateRange(tenantId, start, end, SaleType.SALE);
    }
}


