# API Documentation

**Base URL:** `http://localhost:8081/api`

**Authentication:** Most endpoints require a Bearer token in the Authorization header:
```
Authorization: Bearer <access_token>
```

**Note:** Only `/api/auth/**` endpoints are public and don't require authentication.

---

## 📋 Table of Contents

1. [Authentication Module](#authentication-module)
2. [Products Module](#products-module)
3. [Sales Module](#sales-module)
4. [Stock Module](#stock-module)
5. [Analytics Module](#analytics-module)

---

## 🔐 Authentication Module

**Base Path:** `/api/auth`

All endpoints in this module are **PUBLIC** (no authentication required).

### 1. Register User
- **Method:** `POST`
- **Endpoint:** `/api/auth/register`
- **Auth Required:** ❌ No
- **Request Body:**
```json
{
  "email": "owner@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "companyName": "My Store",
  "role": "OWNER"
}
```
- **Notes:**
  - `role` can be: `OWNER`, `MANAGER`, or `CASHIER`
  - Password must be at least 6 characters
  - Email must be valid format

### 2. Login
- **Method:** `POST`
- **Endpoint:** `/api/auth/login`
- **Auth Required:** ❌ No
- **Request Body:**
```json
{
  "email": "owner@example.com",
  "password": "password123"
}
```

### 3. Refresh Token
- **Method:** `POST`
- **Endpoint:** `/api/auth/refresh`
- **Auth Required:** ❌ No
- **Request Body:**
```json
{
  "refreshToken": "your_refresh_token_here"
}
```

---

## 📦 Products Module

**Base Path:** `/api/products`

All endpoints in this module require **authentication**.

### 1. Create Product
- **Method:** `POST`
- **Endpoint:** `/api/products`
- **Auth Required:** ✅ Yes
- **Request Body:**
```json
{
  "name": "Laptop Computer",
  "sku": "LAP-001",
  "price": 999.99,
  "stockQty": 50,
  "categoryId": 1
}
```
- **Notes:**
  - `categoryId` is optional
  - `stockQty` defaults to 0 if not provided
  - `price` must be positive

### 2. Get All Products
- **Method:** `GET`
- **Endpoint:** `/api/products`
- **Auth Required:** ✅ Yes
- **Query Parameters:**
  - `search` (optional): Search term to filter products
  - `categoryId` (optional): Filter by category ID
- **Examples:**
  - Get all: `/api/products`
  - Search: `/api/products?search=laptop`
  - By category: `/api/products?categoryId=1`

### 3. Get Product by ID
- **Method:** `GET`
- **Endpoint:** `/api/products/{id}`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `id`: Product ID
- **Example:** `/api/products/1`

### 4. Update Product
- **Method:** `PUT`
- **Endpoint:** `/api/products/{id}`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `id`: Product ID
- **Request Body:**
```json
{
  "name": "Updated Laptop Computer",
  "sku": "LAP-001",
  "price": 899.99,
  "categoryId": 1
}
```
- **Notes:**
  - `stockQty` is not included in update (use stock transactions instead)
  - `categoryId` is optional

### 5. Delete Product
- **Method:** `DELETE`
- **Endpoint:** `/api/products/{id}`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `id`: Product ID
- **Example:** `/api/products/1`

### 6. Check SKU Exists
- **Method:** `GET`
- **Endpoint:** `/api/products/sku/{sku}/exists`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `sku`: SKU code to check
- **Example:** `/api/products/sku/LAP-001/exists`
- **Returns:** `true` or `false`

---

## 💰 Sales Module

**Base Path:** `/api/sales`

All endpoints in this module require **authentication**.

### 1. Create Sale
- **Method:** `POST`
- **Endpoint:** `/api/sales`
- **Auth Required:** ✅ Yes
- **Request Body:**
```json
{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```
- **Notes:**
  - Must have at least one item
  - `quantity` must be positive
  - `productId` must exist

### 2. Create Return
- **Method:** `POST`
- **Endpoint:** `/api/sales/{id}/return`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `id`: Sale ID to return
- **Example:** `/api/sales/5/return`
- **Notes:**
  - Creates a return sale for the specified sale
  - No request body required

### 3. Get All Sales
- **Method:** `GET`
- **Endpoint:** `/api/sales`
- **Auth Required:** ✅ Yes
- **Query Parameters:**
  - `startDate` (optional): Start date in ISO format (YYYY-MM-DD)
  - `endDate` (optional): End date in ISO format (YYYY-MM-DD)
- **Examples:**
  - Get all: `/api/sales`
  - By date range: `/api/sales?startDate=2024-01-01&endDate=2024-01-31`

### 4. Get Sale by ID
- **Method:** `GET`
- **Endpoint:** `/api/sales/{id}`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `id`: Sale ID
- **Example:** `/api/sales/1`

---

## 📊 Stock Module

**Base Path:** `/api/stock`

All endpoints in this module require **authentication**.

### 1. Record Stock Receipt
- **Method:** `POST`
- **Endpoint:** `/api/stock/receipt`
- **Auth Required:** ✅ Yes
- **Request Body:**
```json
{
  "productId": 1,
  "quantity": 100,
  "reason": "New shipment received"
}
```
- **Notes:**
  - `quantity` must be positive
  - `reason` is optional

### 2. Record Stock Write-Off
- **Method:** `POST`
- **Endpoint:** `/api/stock/write-off`
- **Auth Required:** ✅ Yes
- **Request Body:**
```json
{
  "productId": 1,
  "quantity": 5,
  "reason": "Damaged goods"
}
```
- **Notes:**
  - `quantity` must be positive
  - `reason` is optional

### 3. Get Product Stock History
- **Method:** `GET`
- **Endpoint:** `/api/stock/product/{productId}/history`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `productId`: Product ID
- **Example:** `/api/stock/product/1/history`

### 4. Get Current Stock
- **Method:** `GET`
- **Endpoint:** `/api/stock/product/{productId}/current`
- **Auth Required:** ✅ Yes
- **Path Parameters:**
  - `productId`: Product ID
- **Example:** `/api/stock/product/1/current`
- **Returns:** Current stock quantity (integer)

---

## 📈 Analytics Module

**Base Path:** `/api/analytics`

All endpoints in this module require **authentication**.

### 1. Get Daily Revenue
- **Method:** `GET`
- **Endpoint:** `/api/analytics/daily/revenue`
- **Auth Required:** ✅ Yes
- **Query Parameters:**
  - `date` (optional): Date in ISO format (YYYY-MM-DD). Defaults to today if not provided
- **Examples:**
  - Today: `/api/analytics/daily/revenue`
  - Specific date: `/api/analytics/daily/revenue?date=2024-01-15`

### 2. Get Daily Sales Count
- **Method:** `GET`
- **Endpoint:** `/api/analytics/daily/sales-count`
- **Auth Required:** ✅ Yes
- **Query Parameters:**
  - `date` (optional): Date in ISO format (YYYY-MM-DD). Defaults to today if not provided
- **Examples:**
  - Today: `/api/analytics/daily/sales-count`
  - Specific date: `/api/analytics/daily/sales-count?date=2024-01-15`

### 3. Get Revenue by Date Range
- **Method:** `GET`
- **Endpoint:** `/api/analytics/revenue`
- **Auth Required:** ✅ Yes
- **Query Parameters:**
  - `startDate` (required): Start date in ISO format (YYYY-MM-DD)
  - `endDate` (required): End date in ISO format (YYYY-MM-DD)
- **Example:** `/api/analytics/revenue?startDate=2024-01-01&endDate=2024-01-31`

### 4. Get Sales Count by Date Range
- **Method:** `GET`
- **Endpoint:** `/api/analytics/sales-count`
- **Auth Required:** ✅ Yes
- **Query Parameters:**
  - `startDate` (required): Start date in ISO format (YYYY-MM-DD)
  - `endDate` (required): End date in ISO format (YYYY-MM-DD)
- **Example:** `/api/analytics/sales-count?startDate=2024-01-01&endDate=2024-01-31`

### 5. Get Dashboard Data
- **Method:** `GET`
- **Endpoint:** `/api/analytics/dashboard`
- **Auth Required:** ✅ Yes
- **Query Parameters:**
  - `date` (optional): Date in ISO format (YYYY-MM-DD). Defaults to today if not provided
- **Examples:**
  - Today: `/api/analytics/dashboard`
  - Specific date: `/api/analytics/dashboard?date=2024-01-15`
- **Returns:** Combined daily revenue and sales count

---

## 📝 Postman Collection Setup Tips

1. **Create Environment Variables:**
   - `base_url`: `http://localhost:8081/api`
   - `access_token`: (will be set after login)
   - `refresh_token`: (will be set after login)

2. **Create Folders by Module:**
   - Authentication
   - Products
   - Sales
   - Stock
   - Analytics

3. **Set Collection Authorization:**
   - Type: Bearer Token
   - Token: `{{access_token}}`

4. **Workflow:**
   - First, call `/api/auth/login` or `/api/auth/register`
   - Extract `accessToken` from response
   - Set it as environment variable `access_token`
   - Use `{{access_token}}` in Authorization header for protected endpoints

---

## 🔑 User Roles

The system supports the following roles:
- `OWNER`: Full access
- `MANAGER`: Management access
- `CASHIER`: Cashier access

---

## ⚠️ Important Notes

1. All dates should be in ISO format: `YYYY-MM-DD` (e.g., `2024-01-15`)
2. All monetary values use `BigDecimal` format (e.g., `999.99`)
3. All endpoints return responses wrapped in `ApiResponse` format:
   ```json
   {
     "success": true,
     "message": "Optional message",
     "data": { ... }
   }
   ```
4. Error responses follow the same structure with `success: false`
5. Most endpoints are tenant-aware (multi-tenant system)
6. Stock quantities are integers
7. Product prices are decimal numbers

---

## 📌 Quick Reference

| Module | Base Path | Auth Required |
|--------|-----------|---------------|
| Authentication | `/api/auth` | ❌ No |
| Products | `/api/products` | ✅ Yes |
| Sales | `/api/sales` | ✅ Yes |
| Stock | `/api/stock` | ✅ Yes |
| Analytics | `/api/analytics` | ✅ Yes |

---

**Last Updated:** Generated from codebase analysis
**Server Port:** 8081
**API Version:** v1
