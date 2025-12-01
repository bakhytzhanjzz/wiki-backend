# Неподключенные эндпойнты для категории "Товары"

> **Для бэкендера:** Этот документ содержит список эндпойнтов, которые описаны в `products-api-endpoints.md`, но еще не подключены на фронтенде. Все эти эндпойнты уже реализованы в `lib/api.ts`, но формы и кнопки на страницах еще не подключены к API.

## Базовый URL
```
https://wiki-backend-30t2.onrender.com/api
```

Все запросы требуют авторизации через Bearer Token.

---

## Статус подключения эндпойнтов

### ✅ Полностью подключено:
- **Products** - GET (список), DELETE (удаление), CREATE (создание)
- **Categories** - GET (список), CREATE (создание)
- **Imports** - GET (список), CREATE (создание)
- **Stores** - GET (список)

### ⚠️ Частично подключено (только GET):
- **Orders** - только GET (список)
- **Inventory** - только GET (список)
- **Transfer** - только GET (список)
- **Repricing** - только GET (список)
- **Write-off** - только GET (список)
- **Suppliers** - только GET (список)

---

## 1. Products (Товары)

### ❌ Не подключено:

#### Получить товар по ID
```
GET /products/:id
```
**Использование:** Для страницы редактирования товара (`/dashboard/products/:id/edit`)

**Response:**
```json
{
  "data": {
    "id": 1,
    "name": "Laptop Computer",
    "sku": "LAP-001",
    "barcode": "1234567890123",
    "price": 999.99,
    "stockQty": 50,
    "categoryName": "Electronics",
    "supplierName": "Tech Supplier",
    "categoryId": 1,
    "supplierId": 1,
    "unit": "шт",
    "description": "Описание товара"
  }
}
```

#### Обновить товар
```
PUT /products/:id
```
**Использование:** Для страницы редактирования товара

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

#### Проверить существование SKU
```
GET /products/sku/:sku/exists
```
**Использование:** В форме создания/редактирования товара для валидации

**Response:**
```json
{
  "exists": true
}
```

**Примечание:** Функция `apiCheckSkuExists` уже импортирована в `app/dashboard/products/new/page.tsx`, но не используется.

---

## 2. Categories (Категории)

### ❌ Не подключено:

#### Получить категорию по ID
```
GET /categories/:id
```

#### Обновить категорию
```
PUT /categories/:id
```

**Body:**
```json
{
  "name": "Electronics"
}
```

#### Удалить категорию
```
DELETE /categories/:id
```

---

## 3. Imports (Импорт)

### ❌ Не подключено:

#### Получить импорт по ID
```
GET /imports/:id
```

#### Обновить импорт
```
PUT /imports/:id
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

#### Удалить импорт
```
DELETE /imports/:id
```

---

## 4. Orders (Заказы)

### ❌ Не подключено (форма есть, но не подключена):

#### Создать заказ
```
POST /orders
```
**Использование:** В компоненте `OrderNewSheet` (кнопка "Создать" не подключена)

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

#### Получить заказ по ID
```
GET /orders/:id
```

#### Обновить заказ
```
PUT /orders/:id
```

#### Удалить заказ
```
DELETE /orders/:id
```

**Примечание:** Форма создания заказа находится в `components/OrderNewSheet.tsx`, но кнопка "Создать" не подключена к API.

---

## 5. Inventory (Инвентаризация)

### ❌ Не подключено (форма есть, но не подключена):

#### Создать инвентаризацию
```
POST /inventory
```
**Использование:** В форме "Новая инвентаризация" на странице `/dashboard/inventory` (кнопка "Начать" не подключена)

**Body:**
```json
{
  "name": "Инвентаризация 2024.01.15 10:30",
  "storeId": 1,
  "type": "full"
}
```

#### Получить инвентаризацию по ID
```
GET /inventory/:id
```

#### Обновить инвентаризацию
```
PUT /inventory/:id
```

#### Завершить инвентаризацию
```
POST /inventory/:id/complete
```
**Использование:** Для завершения инвентаризации (кнопка должна быть в списке или на странице деталей)

#### Удалить инвентаризацию
```
DELETE /inventory/:id
```

**Примечание:** Форма создания находится на странице `app/dashboard/inventory/page.tsx`, но кнопка "Начать" не подключена к API.

---

## 6. Transfer (Трансфер)

### ❌ Не подключено (форма есть, но не подключена):

#### Создать трансфер
```
POST /transfers
```
**Использование:** В форме "Новый трансфер" на странице `/dashboard/transfer` (кнопка "Продолжить" не подключена)

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

#### Получить трансфер по ID
```
GET /transfers/:id
```

#### Обновить трансфер
```
PUT /transfers/:id
```

#### Принять трансфер
```
POST /transfers/:id/receive
```
**Использование:** Для принятия трансфера (кнопка должна быть в списке или на странице деталей)

#### Удалить трансфер
```
DELETE /transfers/:id
```

**Примечание:** Форма создания находится на странице `app/dashboard/transfer/page.tsx`, но кнопка "Продолжить" не подключена к API.

---

## 7. Repricing (Переоценка)

### ❌ Не подключено (форма есть, но не подключена):

#### Создать переоценку
```
POST /repricing
```
**Использование:** В форме "Новая переоценка" на странице `/dashboard/repricing` (кнопка "Продолжить" не подключена)

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

#### Получить переоценку по ID
```
GET /repricing/:id
```

#### Обновить переоценку
```
PUT /repricing/:id
```

#### Удалить переоценку
```
DELETE /repricing/:id
```

**Примечание:** Форма создания находится на странице `app/dashboard/repricing/page.tsx`, но кнопка "Продолжить" не подключена к API.

---

## 8. Write-off (Списание)

### ❌ Не подключено (форма есть, но не подключена):

#### Создать списание
```
POST /write-offs
```
**Использование:** В форме "Новое списание" на странице `/dashboard/writeoff` (кнопка "Продолжить" не подключена)

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

#### Получить списание по ID
```
GET /write-offs/:id
```

#### Обновить списание
```
PUT /write-offs/:id
```

#### Удалить списание
```
DELETE /write-offs/:id
```

**Примечание:** Форма создания находится на странице `app/dashboard/writeoff/page.tsx`, но кнопка "Продолжить" не подключена к API.

---

## 9. Suppliers (Поставщики)

### ❌ Не подключено:

#### Создать поставщика
```
POST /suppliers
```
**Использование:** Кнопка "+ Новый поставщик" на странице `/dashboard/suppliers` (переход на `/dashboard/suppliers/new`, но страница не существует)

**Body:**
```json
{
  "name": "Tech Supplier",
  "phone": "+77001234567",
  "defaultMarkup": 20,
  "note": "Заметка о поставщике"
}
```

#### Получить поставщика по ID
```
GET /suppliers/:id
```

#### Обновить поставщика
```
PUT /suppliers/:id
```

#### Удалить поставщика
```
DELETE /suppliers/:id
```

#### Добавить оплату поставщику
```
POST /suppliers/:id/payments
```
**Использование:** В форме "Добавить оплату" на странице `/dashboard/suppliers` (форма есть, но не подключена)

**Body:**
```json
{
  "amount": 5000.00,
  "paymentMethod": "cash",
  "date": "2024-01-15"
}
```

**Примечание:** 
- Кнопка создания находится на странице `app/dashboard/suppliers/page.tsx`, но ведет на несуществующую страницу.
- Форма добавления оплаты находится на той же странице, но не подключена к API.

---

## Приоритеты подключения

### Высокий приоритет (критично для работы):
1. **Orders** - `POST /orders` (форма уже есть)
2. **Inventory** - `POST /inventory` и `POST /inventory/:id/complete` (форма уже есть)
3. **Transfer** - `POST /transfers` и `POST /transfers/:id/receive` (форма уже есть)
4. **Suppliers** - `POST /suppliers` и `POST /suppliers/:id/payments` (кнопки и формы уже есть)

### Средний приоритет:
5. **Repricing** - `POST /repricing` (форма уже есть)
6. **Write-off** - `POST /write-offs` (форма уже есть)
7. **Products** - `GET /products/:id` и `PUT /products/:id` (для редактирования товаров)

### Низкий приоритет:
8. **Imports** - `PUT /imports/:id` и `DELETE /imports/:id` (для редактирования/удаления)
9. **Categories** - `PUT /categories/:id` и `DELETE /categories/:id` (для управления категориями)
10. **Orders/Inventory/Transfer/Repricing/Write-off** - GET, PUT, DELETE для деталей (для просмотра/редактирования/удаления)

---

## Где находятся формы

Все формы уже созданы, но не подключены к API:

1. **Orders** - `components/OrderNewSheet.tsx` (кнопка "Создать" на строке 167)
2. **Inventory** - `app/dashboard/inventory/page.tsx` (кнопка "Начать" на строке 454)
3. **Transfer** - `app/dashboard/transfer/page.tsx` (кнопка "Продолжить" на строке 487)
4. **Repricing** - `app/dashboard/repricing/page.tsx` (кнопка "Продолжить" на строке 459)
5. **Write-off** - `app/dashboard/writeoff/page.tsx` (кнопка "Продолжить" на строке 453)
6. **Suppliers** - `app/dashboard/suppliers/page.tsx` (кнопка "+ Новый поставщик" на строке 149, форма оплаты на строке 279)

---

## Все API функции уже реализованы

Все необходимые функции уже реализованы в `lib/api.ts`:
- ✅ `apiCreateOrder`
- ✅ `apiGetOrder`
- ✅ `apiUpdateOrder`
- ✅ `apiDeleteOrder`
- ✅ `apiCreateInventory`
- ✅ `apiGetInventoryItem`
- ✅ `apiUpdateInventory`
- ✅ `apiCompleteInventory`
- ✅ `apiDeleteInventory`
- ✅ `apiCreateTransfer`
- ✅ `apiGetTransfer`
- ✅ `apiUpdateTransfer`
- ✅ `apiReceiveTransfer`
- ✅ `apiDeleteTransfer`
- ✅ `apiCreateRepricing`
- ✅ `apiGetRepricingItem`
- ✅ `apiUpdateRepricing`
- ✅ `apiDeleteRepricing`
- ✅ `apiCreateWriteOff`
- ✅ `apiGetWriteOff`
- ✅ `apiUpdateWriteOff`
- ✅ `apiDeleteWriteOff`
- ✅ `apiCreateSupplier`
- ✅ `apiGetSupplier`
- ✅ `apiUpdateSupplier`
- ✅ `apiDeleteSupplier`
- ✅ `apiAddSupplierPayment`
- ✅ `apiGetProduct`
- ✅ `apiUpdateProduct`
- ✅ `apiCheckSkuExists`
- ✅ `apiGetCategory`
- ✅ `apiUpdateCategory`
- ✅ `apiDeleteCategory`
- ✅ `apiGetImport`
- ✅ `apiUpdateImport`
- ✅ `apiDeleteImport`

**Осталось только подключить формы и кнопки к этим функциям!**
