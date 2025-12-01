package com.kz.wiki.service;

import com.kz.wiki.entity.StockTransaction;
import java.util.List;

public interface StockService {
    StockTransaction recordReceipt(Long productId, Integer quantity, String reason, String tenantId, Long userId);
    StockTransaction recordWriteOff(Long productId, Integer quantity, String reason, String tenantId, Long userId);
    List<StockTransaction> getProductHistory(Long productId, String tenantId);
    Integer getCurrentStock(Long productId, String tenantId);
}





