# API Endpoints для раздела "Товары"

> **Для бэкендера:** Этот документ содержит полную спецификацию API эндпойнтов для раздела "Товары". Фронтенд уже реализован и готов к работе с этими эндпойнтами. После реализации эндпойнтов на бэкенде, фронтенд автоматически начнет их использовать.

## Базовый URL
```
https://wiki-backend-30t2.onrender.com/api
```

Все запросы требуют авторизации через Bearer Token (кроме регистрации/логина).

---

## Статус эндпойнтов

### ✅ Уже реализованы (есть в Postman коллекции):
- **Products** (`/products`) - все CRUD операции
- **Categories** (`/categories`) - все CRUD операции
- **Sales** (`/sales`) - получение, создание, возврат
- **Stock** (`/stock`) - поступление, списание, история

### ⚠️ Требуют реализации:
- **Imports** (`/imports`) - CRUD операции
- **Orders** (`/orders`) - CRUD операции (или использовать `/sales`)
- **Inventory** (`/inventory`) - CRUD операции, завершение инвентаризации
- **Transfer** (`/transfers`) - CRUD операции, принятие трансфера
- **Repricing** (`/repricing`) - CRUD операции
- **Write-off** (`/write-offs`) - CRUD операции (или использовать `/stock/write-off`)
- **Suppliers** (`/suppliers`) - CRUD операции, добавление оплаты
- **Stores** (`/stores`) - получение списка магазинов

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

3. **Поиск:** Параметр `search` должен искать по нескольким полям одновременно (например, по ID, наименованию, артикулу, баркоду).

4. **Фильтры:** Все фильтры должны быть опциональными и работать в комбинации.

5. **Даты:** Все даты в формате ISO 8601 (YYYY-MM-DD или YYYY-MM-DDTHH:mm:ssZ).

---

## 1. Каталог (Products)

### Получить все товары
```
GET /products
```

**Query параметры:**
- `search` (string, optional) - поиск по артикулу, баркоду, наименованию
- `categoryId` (number, optional) - фильтр по категории
- `status` (string, optional) - фильтр по статусу (active, inactive, low, zero)

**Пример:**
```
GET /products?search=laptop
GET /products?categoryId=1
GET /products?status=active
```

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
      "stockQty": 50,
      "categoryName": "Electronics",
      "supplierName": "Tech Supplier",
      "categoryId": 1,
      "supplierId": 1
    }
  ]
}
```

### Получить товар по ID
```
GET /products/:id
```

### Создать товар
```
POST /products
```

**Body:**
```json
{
  "name": "Laptop Computer",
  "sku": "LAP-001",
  "barcode": "1234567890123",
  "price": 999.99,
  "stockQty": 50,
  "categoryId": 1,
  "supplierId": 1,
  "unit": "шт",
  "description": "Описание товара"
}
```

### Обновить товар
```
PUT /products/:id
```

**Body:** (те же поля, что и при создании)

### Удалить товар
```
DELETE /products/:id
```

### Проверить существование SKU
```
GET /products/sku/:sku/exists
```

**Response:**
```json
{
  "exists": true
}
```

---

## 2. Категории (Categories)

### Получить все категории
```
GET /categories
```

### Получить категорию по ID
```
GET /categories/:id
```

### Создать категорию
```
POST /categories
```

**Body:**
```json
{
  "name": "Electronics"
}
```

### Обновить категорию
```
PUT /categories/:id
```

### Удалить категорию
```
DELETE /categories/:id
```

---

## 3. Импорт (Imports)

### Получить все импорты
```
GET /imports
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, наименованию, магазину
- `storeId` (number, optional) - фильтр по магазину
- `status` (string, optional) - фильтр по статусу
- `startDate` (string, optional) - дата начала (YYYY-MM-DD)
- `endDate` (string, optional) - дата окончания (YYYY-MM-DD)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Импорт 2024.01.15 10:30",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "quantity": 100,
      "amount": 50000.00,
      "status": "completed",
      "createdAt": "2024-01-15T10:30:00Z",
      "createdBy": "user@example.com"
    }
  ]
}
```

### Получить импорт по ID
```
GET /imports/:id
```

### Создать импорт
```
POST /imports
```

**Body:**
```json
{
  "name": "Импорт 2024.01.15 10:30",
  "storeId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 10,
      "price": 100.00
    }
  ]
}
```

### Обновить импорт
```
PUT /imports/:id
```

### Удалить импорт
```
DELETE /imports/:id
```

---

## 4. Заказы (Orders)

**Примечание:** Заказы могут использовать эндпойнты Sales, если они идентичны.

### Получить все заказы
```
GET /orders
```

**Query параметры:**
- `search` (string, optional) - поиск по номеру заказа, клиенту
- `status` (string, optional) - фильтр по статусу
- `storeId` (number, optional) - фильтр по магазину
- `startDate` (string, optional) - дата начала (YYYY-MM-DD)
- `endDate` (string, optional) - дата окончания (YYYY-MM-DD)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "orderNumber": "ORD-001",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "customerId": 1,
      "customerName": "John Doe",
      "items": [
        {
          "productId": 1,
          "productName": "Laptop",
          "quantity": 2,
          "price": 999.99
        }
      ],
      "totalAmount": 1999.98,
      "status": "pending",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ]
}
```

### Получить заказ по ID
```
GET /orders/:id
```

### Создать заказ
```
POST /orders
```

**Body:**
```json
{
  "storeId": 1,
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "paymentMethod": "cash"
}
```

### Обновить заказ
```
PUT /orders/:id
```

### Удалить заказ
```
DELETE /orders/:id
```

---

## 5. Инвентаризация (Inventory)

### Получить все инвентаризации
```
GET /inventory
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, наименованию, магазину
- `storeId` (number, optional) - фильтр по магазину
- `type` (string, optional) - тип (full, partial)
- `status` (string, optional) - статус (in_progress, completed, cancelled)
- `startDate` (string, optional) - дата начала (YYYY-MM-DD)
- `endDate` (string, optional) - дата окончания (YYYY-MM-DD)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Инвентаризация 2024.01.15 10:30",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "type": "full",
      "quantity": 100,
      "difference": 5,
      "differenceAmount": 500.00,
      "status": "completed",
      "createdAt": "2024-01-15T10:30:00Z",
      "completedAt": "2024-01-15T12:00:00Z"
    }
  ]
}
```

### Получить инвентаризацию по ID
```
GET /inventory/:id
```

### Создать инвентаризацию
```
POST /inventory
```

**Body:**
```json
{
  "name": "Инвентаризация 2024.01.15 10:30",
  "storeId": 1,
  "type": "full"
}
```

### Обновить инвентаризацию
```
PUT /inventory/:id
```

### Завершить инвентаризацию
```
POST /inventory/:id/complete
```

### Удалить инвентаризацию
```
DELETE /inventory/:id
```

---

## 6. Трансфер (Transfer)

### Получить все трансферы
```
GET /transfers
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, наименованию
- `fromStoreId` (number, optional) - магазин отправки
- `toStoreId` (number, optional) - магазин приема
- `status` (string, optional) - статус (pending, in_transit, received, cancelled)
- `startDate` (string, optional) - дата отправки (YYYY-MM-DD)
- `endDate` (string, optional) - дата принятия (YYYY-MM-DD)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Трансфер 2024.01.15 10:30",
      "fromStoreId": 1,
      "fromStoreName": "Store 1",
      "toStoreId": 2,
      "toStoreName": "Store 2",
      "quantity": 50,
      "status": "received",
      "sentAt": "2024-01-15T10:30:00Z",
      "receivedAt": "2024-01-15T14:00:00Z",
      "createdBy": "user@example.com",
      "receivedBy": "user2@example.com"
    }
  ]
}
```

### Получить трансфер по ID
```
GET /transfers/:id
```

### Создать трансфер
```
POST /transfers
```

**Body:**
```json
{
  "name": "Трансфер 2024.01.15 10:30",
  "fromStoreId": 1,
  "toStoreId": 2,
  "items": [
    {
      "productId": 1,
      "quantity": 10
    }
  ],
  "fromFile": false
}
```

### Обновить трансфер
```
PUT /transfers/:id
```

### Принять трансфер
```
POST /transfers/:id/receive
```

### Удалить трансфер
```
DELETE /transfers/:id
```

---

## 7. Переоценка (Repricing)

### Получить все переоценки
```
GET /repricing
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, наименованию
- `storeId` (number, optional) - фильтр по магазину
- `type` (string, optional) - тип переоценки
- `date` (string, optional) - дата (YYYY-MM-DD)
- `userId` (number, optional) - пользователь

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Переоценка 2024.01.15 10:30",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "type": "markup",
      "quantity": 50,
      "createdAt": "2024-01-15T10:30:00Z",
      "createdBy": "user@example.com"
    }
  ]
}
```

### Получить переоценку по ID
```
GET /repricing/:id
```

### Создать переоценку
```
POST /repricing
```

**Body:**
```json
{
  "name": "Переоценка 2024.01.15 10:30",
  "storeId": 1,
  "type": "markup",
  "items": [
    {
      "productId": 1,
      "newPrice": 1099.99
    }
  ],
  "fromFile": false
}
```

### Обновить переоценку
```
PUT /repricing/:id
```

### Удалить переоценку
```
DELETE /repricing/:id
```

---

## 8. Списание (Write-off)

### Получить все списания
```
GET /write-offs
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, наименованию
- `storeId` (number, optional) - фильтр по магазину
- `type` (string, optional) - тип списания
- `startDate` (string, optional) - дата создания (YYYY-MM-DD)
- `endDate` (string, optional) - дата завершения (YYYY-MM-DD)
- `userId` (number, optional) - пользователь

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Списание 2024.01.15 10:30",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "quantity": 5,
      "amount": 500.00,
      "type": "damaged",
      "reason": "Поврежденные товары",
      "createdAt": "2024-01-15T10:30:00Z",
      "createdBy": "user@example.com"
    }
  ]
}
```

### Получить списание по ID
```
GET /write-offs/:id
```

### Создать списание
```
POST /write-offs
```

**Body:**
```json
{
  "name": "Списание 2024.01.15 10:30",
  "storeId": 1,
  "type": "damaged",
  "reason": "Поврежденные товары",
  "items": [
    {
      "productId": 1,
      "quantity": 5
    }
  ],
  "fromFile": false
}
```

**Альтернативный эндпойнт (Stock):**
```
POST /stock/write-off
```

**Body:**
```json
{
  "productId": 1,
  "quantity": 5,
  "reason": "Damaged goods"
}
```

### Обновить списание
```
PUT /write-offs/:id
```

### Удалить списание
```
DELETE /write-offs/:id
```

---

## 9. Поставщики (Suppliers)

### Получить всех поставщиков
```
GET /suppliers
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, имени, телефону

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Tech Supplier",
      "phone": "+77001234567",
      "debtAmount": 10000.00,
      "ordersAmount": 50000.00,
      "paymentsAmount": 40000.00,
      "productsCount": 150
    }
  ]
}
```

### Получить поставщика по ID
```
GET /suppliers/:id
```

### Создать поставщика
```
POST /suppliers
```

**Body:**
```json
{
  "name": "Tech Supplier",
  "phone": "+77001234567",
  "defaultMarkup": 20,
  "note": "Заметка о поставщике"
}
```

### Обновить поставщика
```
PUT /suppliers/:id
```

### Удалить поставщика
```
DELETE /suppliers/:id
```

### Добавить оплату поставщику
```
POST /suppliers/:id/payments
```

**Body:**
```json
{
  "amount": 5000.00,
  "paymentMethod": "cash",
  "date": "2024-01-15"
}
```

---

## 10. Магазины (Stores)

### Получить все магазины
```
GET /stores
```

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Store Madiyar-accessories",
      "address": "Address"
    }
  ]
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
   - `limit` (number) - количество элементов на странице (по умолчанию 10, максимум 100)
   
   **Response с пагинацией:**
   ```json
   {
     "data": [...],
     "pagination": {
       "page": 1,
       "limit": 10,
       "total": 100,
       "totalPages": 10
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
1. **Imports** (`/imports`) - используется на странице импорта
2. **Suppliers** (`/suppliers`) - используется на странице поставщиков
3. **Stores** (`/stores`) - используется для выбора магазинов во всех формах

### Средний приоритет:
4. **Orders** (`/orders`) - можно временно использовать `/sales`
5. **Inventory** (`/inventory`) - используется на странице инвентаризации
6. **Transfer** (`/transfers`) - используется на странице трансферов

### Низкий приоритет:
7. **Repricing** (`/repricing`) - используется на странице переоценки
8. **Write-off** (`/write-offs`) - можно использовать `/stock/write-off` как альтернативу

---

## Примеры использования

### Получение списка товаров с фильтрацией:
```
GET /products?search=laptop&categoryId=1&status=active
```

### Создание импорта:
```
POST /imports
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "Импорт 2024.01.15 10:30",
  "storeId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 10,
      "price": 100.00
    }
  ]
}
```

### Получение поставщиков с поиском:
```
GET /suppliers?search=Tech
```

---

## Контакты

При возникновении вопросов по спецификации API, обращайтесь к фронтенд-разработчику.

