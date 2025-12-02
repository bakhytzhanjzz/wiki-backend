# API Endpoints для раздела "Продажи"

> **Для бэкендера:** Этот документ содержит полную спецификацию API эндпойнтов для раздела "Продажи". Фронтенд уже реализован и готов к работе с этими эндпойнтами. После реализации эндпойнтов на бэкенде, фронтенд автоматически начнет их использовать.

## Базовый URL
```
https://wiki-backend-30t2.onrender.com/api
```

Все запросы требуют авторизации через Bearer Token (кроме регистрации/логина).

---

## Статус эндпойнтов

### ✅ Уже реализованы (есть в Postman коллекции):
- **Sales** (`/sales`) - базовые операции (получение, создание, возврат)

### ⚠️ Требуют реализации:
- **Sales** - расширенные фильтры, статистика, обмены
- **Sales Drafts** (`/sales/drafts`) - черновики и отложки
- **Sales Returns** (`/sales/:id/return`) - возвраты (частично реализовано)
- **Sales Exchanges** (`/sales/:id/exchange`) - обмены
- **Sales Statistics** (`/sales/statistics`) - статистика продаж
- **Customers** (`/customers`) - клиенты для продаж
- **Discounts** (`/discounts`) - скидки и промокоды
- **Gift Cards** (`/gift-cards`) - подарочные карты и сертификаты
- **Payment Methods** (`/payment-methods`) - методы оплаты
- **Sales Reports** (`/sales/reports`) - отчеты по продажам

---

## Важные замечания для бэкендера

1. **Формат ответов:** Все успешные ответы должны возвращать данные в формате:
   ```json
   {
     "data": [...]
   }
   ```
   или для одного объекта:
   ```json
   {
     "data": { ... }
   }
   ```

2. **Обработка ошибок:** При ошибке возвращать:
   ```json
   {
     "message": "Описание ошибки",
     "error": "ERROR_CODE"
   }
   ```

3. **Поиск:** Параметр `search` должен искать по нескольким полям одновременно (ID транзакции, номер чека, имя клиента, имя пользователя).

4. **Фильтры:** Все фильтры должны быть опциональными и работать в комбинации.

5. **Даты:** Все даты в формате ISO 8601 (YYYY-MM-DD или YYYY-MM-DDTHH:mm:ssZ).

---

## 1. Продажи (Sales) - Основные операции

### 1.1. Получить все продажи
```
GET /sales
```

**Query параметры:**
- `search` (string, optional) - поиск по ID транзакции, номеру чека, клиенту, пользователю
- `storeId` (number, optional) - фильтр по магазину
- `paymentMethod` (string, optional) - фильтр по типу оплаты (cash, card, transfer, etc.)
- `sellerId` (number, optional) - фильтр по продавцу
- `customerId` (number, optional) - фильтр по клиенту
- `minAmount` (number, optional) - минимальная сумма чека
- `maxAmount` (number, optional) - максимальная сумма чека
- `startDate` (string, optional) - дата начала (YYYY-MM-DD)
- `endDate` (string, optional) - дата окончания (YYYY-MM-DD)
- `status` (string, optional) - статус (completed, cancelled, draft, deferred)
- `type` (string, optional) - тип транзакции (sale, return, exchange)
- `page` (number, optional) - номер страницы (пагинация)
- `limit` (number, optional) - количество элементов на странице (по умолчанию 20)

**Пример:**
```
GET /sales?search=123&storeId=1&paymentMethod=cash&startDate=2024-01-01&endDate=2024-01-31
GET /sales?minAmount=1000&maxAmount=5000&sellerId=5
```

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "transactionNumber": "TXN-2024-001",
      "receiptNumber": "RCP-2024-001",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "customerId": 1,
      "customerName": "John Doe",
      "customerPhone": "+77001234567",
      "sellerId": 5,
      "sellerName": "Иван Иванов",
      "items": [
        {
          "id": 1,
          "productId": 1,
          "productName": "Laptop Computer",
          "productSku": "LAP-001",
          "productBarcode": "1234567890123",
          "quantity": 2,
          "price": 999.99,
          "discount": 0,
          "discountType": "percentage",
          "totalPrice": 1999.98,
          "type": "product"
        },
        {
          "id": 2,
          "productId": 2,
          "productName": "Mouse",
          "productSku": "MOU-001",
          "quantity": 1,
          "price": 29.99,
          "discount": 10,
          "discountType": "percentage",
          "totalPrice": 26.99,
          "type": "product"
        }
      ],
      "subtotal": 2026.97,
      "discount": 0,
      "discountType": null,
      "discountCode": null,
      "totalAmount": 2026.97,
      "paymentMethod": "cash",
      "paymentMethods": [
        {
          "method": "cash",
          "amount": 2026.97
        }
      ],
      "giftCardsUsed": [],
      "loyaltyPointsUsed": 0,
      "loyaltyPointsEarned": 20,
      "note": "Примечание к продаже",
      "status": "completed",
      "type": "sale",
      "createdAt": "2024-01-15T10:30:00Z",
      "completedAt": "2024-01-15T10:35:00Z",
      "createdBy": 5,
      "createdByName": "Иван Иванов"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 150,
    "totalPages": 8
  }
}
```

### 1.2. Получить продажу по ID
```
GET /sales/:id
```

**Response:** (тот же формат, что и один элемент из списка)

### 1.3. Создать продажу
```
POST /sales
```

**Body:**
```json
{
  "storeId": 1,
  "customerId": 1,
  "sellerId": 5,
  "useWholesalePrices": false,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 999.99,
      "wholesalePrice": 899.99,
      "discount": 0,
      "discountType": "percentage"
    },
    {
      "productId": 2,
      "quantity": 1,
      "price": 29.99,
      "wholesalePrice": 25.99,
      "discount": 10,
      "discountType": "percentage"
    }
  ],
  "subtotal": 2026.97,
  "discount": 0,
  "discountType": null,
  "discountCode": null,
  "totalAmount": 2026.97,
  "paymentMethod": "cash",
  "paymentMethods": [
    {
      "method": "cash",
      "amount": 2026.97
    }
  ],
  "giftCardCodes": [],
  "loyaltyPointsUsed": 0,
  "debtPaymentAmount": 0,
  "note": "Примечание к продаже",
  "isDraft": false,
  "isDeferred": false
}
```

**Примечания:**
- `useWholesalePrices` (boolean, optional) - использовать оптовые цены вместо розничных
- Если `useWholesalePrices: true`, то в расчетах используются `wholesalePrice` вместо `price`
- `wholesalePrice` может быть указан для каждого товара, если он отличается от розничной цены

**Response:** (созданная продажа в формате из 1.1)

### 1.4. Обновить продажу (только для черновиков и отложок)
```
PUT /sales/:id
```

**Body:** (те же поля, что и при создании)

### 1.5. Удалить продажу (только для черновиков и отложок)
```
DELETE /sales/:id
```

### 1.6. Отменить продажу
```
POST /sales/:id/cancel
```

**Body:**
```json
{
  "reason": "Причина отмены"
}
```

---

## 2. Возвраты (Returns)

### 2.1. Создать возврат
```
POST /sales/:id/return
```

**Body:**
```json
{
  "items": [
    {
      "saleItemId": 1,
      "quantity": 1,
      "reason": "Не подошел размер"
    }
  ],
  "refundMethod": "cash",
  "note": "Примечание к возврату"
}
```

**Response:** (новая транзакция типа "return")

### 2.2. Получить все возвраты
```
GET /sales/returns
```

**Query параметры:** (те же, что и для GET /sales, но type автоматически = "return")

### 2.3. Получить возврат по ID
```
GET /sales/returns/:id
```

---

## 3. Обмены (Exchanges)

### 3.1. Создать обмен
```
POST /sales/:id/exchange
```

**Body:**
```json
{
  "returnItems": [
    {
      "saleItemId": 1,
      "quantity": 1,
      "reason": "Не подошел размер"
    }
  ],
  "newItems": [
    {
      "productId": 2,
      "quantity": 1,
      "price": 29.99
    }
  ],
  "differenceAmount": 0,
  "paymentMethod": "cash",
  "note": "Примечание к обмену"
}
```

**Response:** (новая транзакция типа "exchange")

### 3.2. Получить все обмены
```
GET /sales/exchanges
```

**Query параметры:** (те же, что и для GET /sales, но type автоматически = "exchange")

### 3.3. Получить обмен по ID
```
GET /sales/exchanges/:id
```

---

## 4. Черновики и отложки (Drafts & Deferred)

### 4.1. Получить все черновики
```
GET /sales/drafts
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, клиенту, пользователю
- `storeId` (number, optional) - фильтр по магазину
- `sellerId` (number, optional) - фильтр по продавцу
- `customerId` (number, optional) - фильтр по клиенту
- `startDate` (string, optional) - дата начала
- `endDate` (string, optional) - дата окончания
- `page` (number, optional)
- `limit` (number, optional)

**Response:** (список продаж со статусом "draft")

### 4.2. Получить все отложки
```
GET /sales/deferred
```

**Query параметры:** (те же, что и для черновиков)

**Response:** (список продаж со статусом "deferred")

### 4.3. Сохранить как черновик
```
POST /sales/drafts
```

**Body:** (тот же формат, что и при создании продажи, но `isDraft: true`)

### 4.4. Сохранить как отложку
```
POST /sales/deferred
```

**Body:** (тот же формат, что и при создании продажи, но `isDeferred: true`)

### 4.5. Завершить черновик/отложку (преобразовать в продажу)
```
POST /sales/drafts/:id/complete
POST /sales/deferred/:id/complete
```

**Body:** (может содержать обновленные данные перед завершением)

---

## 5. Статистика продаж (Statistics)

### 5.1. Получить статистику продаж
```
GET /sales/statistics
```

**Query параметры:**
- `storeId` (number, optional) - фильтр по магазину
- `sellerId` (number, optional) - фильтр по продавцу
- `startDate` (string, optional) - дата начала (YYYY-MM-DD)
- `endDate` (string, optional) - дата окончания (YYYY-MM-DD)
- `groupBy` (string, optional) - группировка (day, week, month, year, seller, store, paymentMethod)

**Response:**
```json
{
  "data": {
    "totalTransactions": 150,
    "totalAmount": 250000.00,
    "totalProducts": 450,
    "totalServices": 20,
    "totalKits": 15,
    "totalCertificates": 5,
    "totalReturns": 10,
    "totalReturnsAmount": 5000.00,
    "totalExchanges": 5,
    "totalExchangesAmount": 2000.00,
    "totalGiftCardsUsed": 8,
    "totalGiftCardsAmount": 4000.00,
    "totalLoyaltyPointsEarned": 2500,
    "totalLoyaltyPointsUsed": 500,
    "totalDebtPayments": 3000.00,
    "customerBalance": {
      "totalAccrued": 5000.00,
      "totalSpent": 2000.00,
      "currentBalance": 3000.00
    },
    "byPaymentMethod": [
      {
        "method": "cash",
        "count": 80,
        "amount": 120000.00
      },
      {
        "method": "card",
        "count": 70,
        "amount": 130000.00
      }
    ],
    "byType": [
      {
        "type": "product",
        "count": 450,
        "amount": 200000.00
      },
      {
        "type": "service",
        "count": 20,
        "amount": 30000.00
      },
      {
        "type": "kit",
        "count": 15,
        "amount": 15000.00
      },
      {
        "type": "certificate",
        "count": 5,
        "amount": 5000.00
      }
    ]
  }
}
```

### 5.2. Получить статистику по датам (для графиков)
```
GET /sales/statistics/by-date
```

**Query параметры:**
- `storeId` (number, optional)
- `startDate` (string, required) - дата начала (YYYY-MM-DD)
- `endDate` (string, required) - дата окончания (YYYY-MM-DD)
- `groupBy` (string, optional) - группировка (day, week, month) - по умолчанию "day"

**Response:**
```json
{
  "data": [
    {
      "date": "2024-01-15",
      "transactions": 10,
      "amount": 15000.00,
      "products": 25,
      "services": 2
    },
    {
      "date": "2024-01-16",
      "transactions": 12,
      "amount": 18000.00,
      "products": 30,
      "services": 1
    }
  ]
}
```

---

## 6. Клиенты (Customers)

### 6.1. Получить всех клиентов
```
GET /customers
```

**Query параметры:**
- `search` (string, optional) - поиск по имени, телефону, email, номеру карты
- `storeId` (number, optional) - фильтр по магазину
- `hasDebt` (boolean, optional) - только с долгами
- `hasLoyaltyPoints` (boolean, optional) - только с баллами лояльности
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "phone": "+77001234567",
      "email": "john@example.com",
      "cardNumber": "1234567890",
      "loyaltyPoints": 250,
      "totalPurchases": 50000.00,
      "totalTransactions": 25,
      "lastPurchaseDate": "2024-01-15T10:30:00Z",
      "debtAmount": 0,
      "notes": "VIP клиент",
      "createdAt": "2023-06-01T10:00:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 100,
    "totalPages": 5
  }
}
```

### 6.2. Получить клиента по ID
```
GET /customers/:id
```

### 6.3. Создать клиента
```
POST /customers
```

**Body:**
```json
{
  "name": "John Doe",
  "phone": "+77001234567",
  "email": "john@example.com",
  "cardNumber": "1234567890",
  "notes": "VIP клиент"
}
```

### 6.4. Обновить клиента
```
PUT /customers/:id
```

**Body:** (те же поля, что и при создании)

### 6.5. Удалить клиента
```
DELETE /customers/:id
```

### 6.6. Получить историю покупок клиента
```
GET /customers/:id/purchases
```

**Query параметры:**
- `startDate` (string, optional)
- `endDate` (string, optional)
- `page` (number, optional)
- `limit` (number, optional)

**Response:** (список продаж клиента)

### 6.7. Получить баланс клиента (баллы лояльности и долги)
```
GET /customers/:id/balance
```

**Response:**
```json
{
  "data": {
    "loyaltyPoints": 250,
    "debtAmount": 0,
    "totalAccrued": 5000.00,
    "totalSpent": 2000.00,
    "currentBalance": 3000.00
  }
}
```

### 6.8. Поиск клиента (быстрый поиск для добавления в продажу)
```
GET /customers/search
```

**Query параметры:**
- `q` (string, required) - поисковый запрос (имя, телефон, номер карты, email)
- `limit` (number, optional) - количество результатов (по умолчанию 10, максимум 20)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "phone": "+77001234567",
      "email": "john@example.com",
      "cardNumber": "1234567890",
      "loyaltyPoints": 250,
      "debtAmount": 0
    }
  ]
}
```

### 6.9. Погасить долг клиента
```
POST /customers/:id/repay-debt
```

**Body:**
```json
{
  "amount": 1000.00,
  "paymentMethod": "cash",
  "saleId": 1,
  "note": "Погашение долга при продаже"
}
```

**Response:**
```json
{
  "data": {
    "id": 1,
    "customerId": 1,
    "amount": 1000.00,
    "paymentMethod": "cash",
    "saleId": 1,
    "remainingDebt": 0,
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 6.10. Получить долги клиента
```
GET /customers/:id/debts
```

**Query параметры:**
- `status` (string, optional) - статус (all, unpaid, paid, partial, overdue)
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "saleId": 1,
      "saleNumber": "TXN-2024-001",
      "amount": 5000.00,
      "paidAmount": 2000.00,
      "remainingAmount": 3000.00,
      "status": "partial",
      "dueDate": "2024-02-01",
      "isOverdue": false,
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 5,
    "totalPages": 1
  }
}
```

---

## 7. Скидки и промокоды (Discounts & Promo Codes)

### 7.1. Получить все промокоды
```
GET /discounts
```

**Query параметры:**
- `search` (string, optional) - поиск по коду, названию
- `isActive` (boolean, optional) - только активные
- `type` (string, optional) - тип (percentage, fixed, free_shipping)
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "code": "SUMMER2024",
      "name": "Летняя скидка",
      "type": "percentage",
      "value": 15,
      "minPurchaseAmount": 1000,
      "maxDiscountAmount": 500,
      "applicableTo": "all",
      "applicableProductIds": [],
      "applicableCategoryIds": [],
      "startDate": "2024-06-01T00:00:00Z",
      "endDate": "2024-08-31T23:59:59Z",
      "usageLimit": 100,
      "usageCount": 45,
      "isActive": true,
      "createdAt": "2024-05-01T10:00:00Z"
    }
  ]
}
```

### 7.2. Получить промокод по ID
```
GET /discounts/:id
```

### 7.3. Проверить промокод
```
POST /discounts/validate
```

**Body:**
```json
{
  "code": "SUMMER2024",
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 999.99
    }
  ],
  "subtotal": 1999.98
}
```

**Response:**
```json
{
  "data": {
    "valid": true,
    "discount": {
      "id": 1,
      "code": "SUMMER2024",
      "name": "Летняя скидка",
      "type": "percentage",
      "value": 15,
      "discountAmount": 299.997,
      "finalAmount": 1699.983
    }
  }
}
```

### 7.4. Создать промокод
```
POST /discounts
```

**Body:**
```json
{
  "code": "SUMMER2024",
  "name": "Летняя скидка",
  "type": "percentage",
  "value": 15,
  "minPurchaseAmount": 1000,
  "maxDiscountAmount": 500,
  "applicableTo": "all",
  "applicableProductIds": [],
  "applicableCategoryIds": [],
  "startDate": "2024-06-01T00:00:00Z",
  "endDate": "2024-08-31T23:59:59Z",
  "usageLimit": 100,
  "isActive": true
}
```

### 7.5. Обновить промокод
```
PUT /discounts/:id
```

### 6.6. Удалить промокод
```
DELETE /discounts/:id
```

### 7.7. Применить скидку вручную (для продавца)
```
POST /sales/apply-discount
```

**Body:**
```json
{
  "saleId": 1,
  "discountType": "percentage",
  "discountValue": 10,
  "note": "Ручная скидка продавца"
}
```

---

## 8. Подарочные карты и сертификаты (Gift Cards & Certificates)

### 8.1. Получить все подарочные карты
```
GET /gift-cards
```

**Query параметры:**
- `search` (string, optional) - поиск по коду, номеру
- `status` (string, optional) - статус (active, used, expired, cancelled)
- `type` (string, optional) - тип (certificate, voucher)
- `storeId` (number, optional) - фильтр по магазину
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "code": "GC-1234567890",
      "number": "1234567890",
      "type": "certificate",
      "amount": 5000.00,
      "remainingAmount": 3000.00,
      "status": "active",
      "issuedAt": "2024-01-01T10:00:00Z",
      "expiresAt": "2025-01-01T23:59:59Z",
      "issuedBy": 1,
      "issuedByName": "Admin",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "usedInSales": [1, 5, 10],
      "createdAt": "2024-01-01T10:00:00Z"
    }
  ]
}
```

### 8.2. Получить подарочную карту по ID или коду
```
GET /gift-cards/:id
GET /gift-cards/code/:code
```

### 8.3. Проверить подарочную карту
```
POST /gift-cards/validate
```

**Body:**
```json
{
  "code": "GC-1234567890"
}
```

**Response:**
```json
{
  "data": {
    "valid": true,
    "giftCard": {
      "id": 1,
      "code": "GC-1234567890",
      "type": "certificate",
      "amount": 5000.00,
      "remainingAmount": 3000.00,
      "status": "active",
      "expiresAt": "2025-01-01T23:59:59Z"
    }
  }
}
```

### 8.4. Создать подарочную карту
```
POST /gift-cards
```

**Body:**
```json
{
  "type": "certificate",
  "amount": 5000.00,
  "expiresAt": "2025-01-01T23:59:59Z",
  "storeId": 1,
  "note": "Подарочный сертификат"
}
```

### 8.5. Использовать подарочную карту в продаже
```
POST /gift-cards/:id/use
```

**Body:**
```json
{
  "saleId": 1,
  "amount": 2000.00
}
```

### 8.6. Отменить использование подарочной карты
```
POST /gift-cards/:id/refund
```

**Body:**
```json
{
  "saleId": 1,
  "amount": 2000.00
}
```

### 8.7. Обновить подарочную карту
```
PUT /gift-cards/:id
```

### 8.8. Удалить подарочную карту
```
DELETE /gift-cards/:id
```

---

## 9. Продавцы (Sellers)

### 9.1. Получить всех продавцов
```
GET /sellers
```

**Query параметры:**
- `search` (string, optional) - поиск по имени, email
- `storeId` (number, optional) - фильтр по магазину
- `isActive` (boolean, optional) - только активные
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 5,
      "name": "Иван Иванов",
      "email": "ivan@example.com",
      "phone": "+77001234567",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "role": "seller",
      "isActive": true,
      "totalSales": 150,
      "totalAmount": 250000.00,
      "createdAt": "2023-06-01T10:00:00Z"
    }
  ]
}
```

### 9.2. Получить продавца по ID
```
GET /sellers/:id
```

### 9.3. Получить статистику продавца
```
GET /sellers/:id/statistics
```

**Query параметры:**
- `startDate` (string, optional)
- `endDate` (string, optional)

**Response:**
```json
{
  "data": {
    "sellerId": 5,
    "sellerName": "Иван Иванов",
    "totalTransactions": 150,
    "totalAmount": 250000.00,
    "averageTransaction": 1666.67,
    "totalProducts": 450,
    "totalServices": 20,
    "period": {
      "startDate": "2024-01-01",
      "endDate": "2024-01-31"
    }
  }
}
```

### 9.4. Получить продавцов для магазина
```
GET /sellers/by-store/:storeId
```

**Query параметры:**
- `isActive` (boolean, optional) - только активные

**Response:**
```json
{
  "data": [
    {
      "id": 5,
      "name": "Иван Иванов",
      "email": "ivan@example.com",
      "phone": "+77001234567",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "role": "seller",
      "isActive": true
    }
  ]
}
```

---

## 10. Методы оплаты (Payment Methods)

### 9.1. Получить все методы оплаты
```
GET /payment-methods
```

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "code": "cash",
      "name": "Наличные",
      "icon": "💵",
      "isActive": true,
      "sortOrder": 1
    },
    {
      "id": 2,
      "code": "card",
      "name": "Банковская карта",
      "icon": "💳",
      "isActive": true,
      "sortOrder": 2
    },
    {
      "id": 3,
      "code": "transfer",
      "name": "Перевод",
      "icon": "📱",
      "isActive": true,
      "sortOrder": 3
    },
    {
      "id": 4,
      "code": "gift_card",
      "name": "Подарочная карта",
      "icon": "🎁",
      "isActive": true,
      "sortOrder": 4
    }
  ]
}
```

### 9.2. Получить метод оплаты по ID
```
GET /payment-methods/:id
```

### 9.3. Создать метод оплаты
```
POST /payment-methods
```

**Body:**
```json
{
  "code": "cash",
  "name": "Наличные",
  "icon": "💵",
  "isActive": true,
  "sortOrder": 1
}
```

### 9.4. Обновить метод оплаты
```
PUT /payment-methods/:id
```

### 9.5. Удалить метод оплаты
```
DELETE /payment-methods/:id
```

---

## 11. Отчеты по продажам (Reports)

### 10.1. Сгенерировать отчет по продажам (PDF/Excel)
```
GET /sales/reports
```

**Query параметры:**
- `format` (string, optional) - формат отчета (pdf, excel) - по умолчанию "pdf"
- `storeId` (number, optional)
- `sellerId` (number, optional)
- `startDate` (string, required)
- `endDate` (string, required)
- `includeDetails` (boolean, optional) - включать детали по товарам
- `groupBy` (string, optional) - группировка (day, seller, product, category)

**Response:** (файл PDF или Excel)

### 10.2. Получить отчет по продавцам
```
GET /sales/reports/sellers
```

**Query параметры:**
- `storeId` (number, optional)
- `startDate` (string, required)
- `endDate` (string, required)

**Response:**
```json
{
  "data": [
    {
      "sellerId": 5,
      "sellerName": "Иван Иванов",
      "totalTransactions": 50,
      "totalAmount": 75000.00,
      "averageTransaction": 1500.00,
      "totalProducts": 150,
      "totalServices": 10
    }
  ]
}
```

### 10.3. Получить отчет по товарам
```
GET /sales/reports/products
```

**Query параметры:**
- `storeId` (number, optional)
- `startDate` (string, required)
- `endDate` (string, required)
- `categoryId` (number, optional)
- `sortBy` (string, optional) - сортировка (quantity, amount) - по умолчанию "quantity"
- `limit` (number, optional) - топ N товаров

**Response:**
```json
{
  "data": [
    {
      "productId": 1,
      "productName": "Laptop Computer",
      "productSku": "LAP-001",
      "categoryName": "Electronics",
      "totalQuantity": 100,
      "totalAmount": 99999.00,
      "averagePrice": 999.99,
      "transactionsCount": 50
    }
  ]
}
```

### 10.4. Получить отчет по клиентам
```
GET /sales/reports/customers
```

**Query параметры:**
- `storeId` (number, optional)
- `startDate` (string, required)
- `endDate` (string, required)
- `sortBy` (string, optional) - сортировка (amount, transactions) - по умолчанию "amount"
- `limit` (number, optional) - топ N клиентов

**Response:**
```json
{
  "data": [
    {
      "customerId": 1,
      "customerName": "John Doe",
      "customerPhone": "+77001234567",
      "totalTransactions": 25,
      "totalAmount": 50000.00,
      "averageTransaction": 2000.00,
      "lastPurchaseDate": "2024-01-15T10:30:00Z"
    }
  ]
}
```

---

## 12. Дополнительные операции

### 11.1. Поиск товаров для продажи
```
GET /products/search
```

**Query параметры:**
- `q` (string, required) - поисковый запрос (артикул, баркод, наименование)
- `storeId` (number, optional) - фильтр по магазину (для проверки наличия)
- `inStock` (boolean, optional) - только в наличии
- `includeWholesalePrice` (boolean, optional) - включить оптовую цену в ответ

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Laptop Computer",
      "sku": "LAP-001",
      "barcode": "1234567890123",
      "price": 999.99,
      "wholesalePrice": 899.99,
      "stockQty": 50,
      "categoryName": "Electronics",
      "supplierName": "Tech Supplier",
      "availableInStore": true,
      "storeStockQty": 10
    }
  ]
}
```

### 11.2. Получить текущий номер транзакции
```
GET /sales/next-transaction-number
```

**Query параметры:**
- `storeId` (number, optional)

**Response:**
```json
{
  "data": {
    "transactionNumber": "TXN-2024-001",
    "receiptNumber": "RCP-2024-001"
  }
}
```

### 11.3. Печать чека
```
POST /sales/:id/print
```

**Query параметры:**
- `format` (string, optional) - формат (58mm, 80mm, a4) - по умолчанию "58mm"

**Response:** (PDF файл чека)

### 11.4. Отправить чек на email/SMS
```
POST /sales/:id/send-receipt
```

**Body:**
```json
{
  "email": "customer@example.com",
  "phone": "+77001234567",
  "method": "email"
}
```

### 11.5. Получить список продавцов для выбора (при создании продажи)
```
GET /sellers/active
```

**Query параметры:**
- `storeId` (number, optional) - фильтр по магазину

**Response:**
```json
{
  "data": [
    {
      "id": 5,
      "name": "Иван Иванов",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "isActive": true
    }
  ]
}
```

### 11.6. Погашение долгов клиента при продаже
```
POST /sales/:id/repay-customer-debt
```

**Body:**
```json
{
  "customerId": 1,
  "amount": 1000.00,
  "paymentMethod": "cash",
  "note": "Погашение долга при продаже"
}
```

**Response:**
```json
{
  "data": {
    "saleId": 1,
    "debtRepaymentId": 1,
    "customerId": 1,
    "amount": 1000.00,
    "remainingDebt": 0,
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

---

## Общие замечания

1. **Авторизация:** Все запросы (кроме `/auth/login` и `/auth/register`) требуют Bearer Token в заголовке:
   ```
   Authorization: Bearer <access_token>
   ```

2. **Формат дат:** Используйте формат ISO 8601 (YYYY-MM-DD или YYYY-MM-DDTHH:mm:ssZ)

3. **Пагинация:** Если эндпойнт поддерживает пагинацию, используйте параметры:
   - `page` (number) - номер страницы (начинается с 1)
   - `limit` (number) - количество элементов на странице (по умолчанию 20, максимум 100)
   
   **Response с пагинацией:**
   ```json
   {
     "data": [...],
     "pagination": {
       "page": 1,
       "limit": 20,
       "total": 100,
       "totalPages": 5
     }
   }
   ```

4. **Ошибки:** При ошибке API возвращает:
   ```json
   {
     "message": "Error message",
     "error": "Error code"
   }
   ```

5. **Статусы ответов:**
   - `200` - успешный запрос (GET, PUT, DELETE)
   - `201` - успешное создание (POST)
   - `400` - ошибка валидации
   - `401` - не авторизован (отсутствует или невалидный токен)
   - `403` - нет доступа (недостаточно прав)
   - `404` - не найдено
   - `500` - внутренняя ошибка сервера

6. **Валидация:** Все обязательные поля должны проверяться на бэкенде. При отсутствии обязательного поля возвращать `400` с описанием ошибки.

7. **Типы данных:**
   - `id` - number или string (в зависимости от реализации)
   - `price`, `amount` - number (decimal, 2 знака после запятой)
   - `quantity` - number (integer)
   - `date` - string (ISO 8601)
   - `status` - string (enum значений)

8. **CORS:** Бэкенд должен разрешать запросы с фронтенда (уже настроено через Next.js proxy `/api/backend`).

---

## Приоритеты реализации

### Высокий приоритет (критично для работы):
1. **Sales CRUD** (`/sales`) - базовые операции создания, получения, обновления продаж
2. **Sales Search & Filters** (`/sales` с query параметрами) - поиск и фильтрация продаж
3. **Sales Statistics** (`/sales/statistics`) - статистика для правой колонки на странице продаж
4. **Customers** (`/customers`) - управление клиентами
5. **Payment Methods** (`/payment-methods`) - методы оплаты
6. **Products Search** (`/products/search`) - поиск товаров для добавления в корзину

### Средний приоритет:
7. **Sales Returns** (`/sales/:id/return`) - возвраты
8. **Sales Exchanges** (`/sales/:id/exchange`) - обмены
9. **Sales Drafts** (`/sales/drafts`) - черновики
10. **Sales Deferred** (`/sales/deferred`) - отложки
11. **Discounts** (`/discounts`) - промокоды и скидки
12. **Gift Cards** (`/gift-cards`) - подарочные карты

### Низкий приоритет:
13. **Sales Reports** (`/sales/reports`) - отчеты
14. **Print Receipt** (`/sales/:id/print`) - печать чеков
15. **Send Receipt** (`/sales/:id/send-receipt`) - отправка чеков

---

## Примеры использования

### Создание продажи с клиентом и скидкой:
```
POST /sales
Content-Type: application/json
Authorization: Bearer <token>

{
  "storeId": 1,
  "customerId": 1,
  "sellerId": 5,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 999.99,
      "discount": 0,
      "discountType": "percentage"
    }
  ],
  "subtotal": 1999.98,
  "discount": 10,
  "discountType": "percentage",
  "discountCode": "SUMMER2024",
  "totalAmount": 1799.98,
  "paymentMethod": "cash",
  "paymentMethods": [
    {
      "method": "cash",
      "amount": 1799.98
    }
  ],
  "note": "Продажа с промокодом"
}
```

### Получение продаж с фильтрами:
```
GET /sales?storeId=1&paymentMethod=cash&startDate=2024-01-01&endDate=2024-01-31&minAmount=1000&maxAmount=5000
Authorization: Bearer <token>
```

### Создание возврата:
```
POST /sales/1/return
Content-Type: application/json
Authorization: Bearer <token>

{
  "items": [
    {
      "saleItemId": 1,
      "quantity": 1,
      "reason": "Не подошел размер"
    }
  ],
  "refundMethod": "cash",
  "note": "Возврат товара"
}
```

### Проверка промокода:
```
POST /discounts/validate
Content-Type: application/json
Authorization: Bearer <token>

{
  "code": "SUMMER2024",
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 999.99
    }
  ],
  "subtotal": 1999.98
}
```

### Использование подарочной карты:
```
POST /gift-cards/validate
Content-Type: application/json
Authorization: Bearer <token>

{
  "code": "GC-1234567890"
}
```

---

## Контакты

При возникновении вопросов по спецификации API, обращайтесь к фронтенд-разработчику.

