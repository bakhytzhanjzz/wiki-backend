# API Endpoints для раздела "Клиенты"

> **Для бэкендера:** Этот документ содержит полную спецификацию API эндпойнтов для раздела "Клиенты". Фронтенд уже реализован и готов к работе с этими эндпойнтами. После реализации эндпойнтов на бэкенде, фронтенд автоматически начнет их использовать.

## Базовый URL
```
https://wiki-backend-30t2.onrender.com/api
```

Все запросы требуют авторизации через Bearer Token.

---

## 1. Клиенты (Clients)

### 1.1. Получить список клиентов
```
GET /clients
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, имени, телефону
- `groupIds` (number[], optional) - фильтр по группам (можно передать несколько)
- `tagIds` (number[], optional) - фильтр по тегам (можно передать несколько)
- `birthdayFrom` (string, optional) - дата начала дня рождения (YYYY-MM-DD)
- `birthdayTo` (string, optional) - дата окончания дня рождения (YYYY-MM-DD)
- `purchaseAmountFrom` (number, optional) - минимальная сумма покупок
- `purchaseAmountTo` (number, optional) - максимальная сумма покупок
- `lastPurchaseFrom` (string, optional) - дата начала последней покупки (YYYY-MM-DD)
- `lastPurchaseTo` (string, optional) - дата окончания последней покупки (YYYY-MM-DD)
- `noPurchaseDays` (number, optional) - количество дней без покупок
- `registrationFrom` (string, optional) - дата начала регистрации (YYYY-MM-DD)
- `registrationTo` (string, optional) - дата окончания регистрации (YYYY-MM-DD)
- `registrationStoreIds` (number[], optional) - фильтр по магазинам регистрации
- `gender` (string, optional) - фильтр по полу ("male" | "female")
- `page` (number, optional) - номер страницы
- `limit` (number, optional) - количество элементов на странице

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "firstName": "Иван",
      "lastName": "Иванов",
      "middleName": "Иванович",
      "fullName": "Иванов Иван Иванович",
      "phone": "+77001234567",
      "phones": ["+77001234567", "+77001234568"],
      "email": "ivan@example.com",
      "birthday": "1990-05-15",
      "gender": "male",
      "maritalStatus": "married",
      "language": "ru",
      "groups": [
        { "id": 1, "name": "VIP" }
      ],
      "tags": [
        { "id": 1, "name": "Постоянный клиент" }
      ],
      "totalPurchases": 150000.00,
      "lastPurchaseDate": "2024-01-15T10:30:00Z",
      "registrationDate": "2023-06-01T10:00:00Z",
      "registrationStoreId": 1,
      "registrationStoreName": "Store Madiyar-accessories",
      "debtAmount": 0,
      "loyaltyPoints": 250,
      "loyaltyLevel": "Gold",
      "addresses": [
        {
          "id": 1,
          "address": "ул. Абая, 150",
          "city": "Алматы",
          "region": "Алматы",
          "postalCode": "050000"
        }
      ],
      "socialNetworks": {
        "telegram": "@ivan",
        "facebook": "ivan.ivanov",
        "instagram": "ivan_ivanov"
      },
      "relatives": [
        {
          "id": 1,
          "name": "Мария Иванова",
          "relation": "Жена",
          "phone": "+77001234569"
        }
      ],
      "notifications": {
        "sms": true,
        "phone": false,
        "social": true,
        "email": true
      },
      "cards": [
        {
          "id": 1,
          "type": "passport",
          "number": "123456789",
          "fileUrl": "https://example.com/files/passport.jpg"
        }
      ],
      "createdAt": "2023-06-01T10:00:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
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

### 1.2. Получить клиента по ID
```
GET /clients/:id
```

**Response:** (тот же формат, что и в списке, но один объект)

### 1.3. Получить статистику клиентов
```
GET /clients/statistics
```

**Response:**
```json
{
  "data": {
    "totalClients": 1250,
    "newClientsLastWeek": 45,
    "nonReturningClients": 120,
    "birthdaysToday": 5
  }
}
```

### 1.4. Создать клиента
```
POST /clients
```

**Body:**
```json
{
  "firstName": "Иван",
  "lastName": "Иванов",
  "middleName": "Иванович",
  "phone": "+77001234567",
  "phones": ["+77001234567"],
  "email": "ivan@example.com",
  "birthday": "1990-05-15",
  "gender": "male",
  "maritalStatus": "married",
  "language": "ru",
  "groupIds": [1],
  "tagIds": [1],
  "addresses": [
    {
      "address": "ул. Абая, 150",
      "city": "Алматы",
      "region": "Алматы",
      "postalCode": "050000"
    }
  ],
  "socialNetworks": {
    "telegram": "@ivan",
    "facebook": "ivan.ivanov",
    "instagram": "ivan_ivanov"
  },
  "relatives": [
    {
      "name": "Мария Иванова",
      "relation": "Жена",
      "phone": "+77001234569"
    }
  ],
  "notifications": {
    "sms": true,
    "phone": false,
    "social": true,
    "email": true
  }
}
```

**Response:** (созданный клиент)

### 1.5. Обновить клиента
```
PUT /clients/:id
```

**Body:** (те же поля, что и при создании)

**Response:** (обновленный клиент)

### 1.6. Удалить клиента
```
DELETE /clients/:id
```

**Response:**
```json
{
  "message": "Клиент успешно удален"
}
```

### 1.7. Массовое обновление клиентов
```
POST /clients/bulk-update
```

**Body:**
```json
{
  "clientIds": [1, 2, 3],
  "updates": {
    "language": "kk",
    "notifications": {
      "sms": true
    }
  }
}
```

**Response:**
```json
{
  "data": {
    "updated": 3
  }
}
```

### 1.8. Массовое присвоение групп
```
POST /clients/bulk-assign-groups
```

**Body:**
```json
{
  "clientIds": [1, 2, 3],
  "groupIds": [1, 2]
}
```

**Response:**
```json
{
  "data": {
    "updated": 3
  }
}
```

### 1.9. Массовое удаление групп
```
POST /clients/bulk-remove-groups
```

**Body:**
```json
{
  "clientIds": [1, 2, 3],
  "groupIds": [1, 2]
}
```

**Response:**
```json
{
  "data": {
    "updated": 3
  }
}
```

### 1.10. Массовое присвоение тегов
```
POST /clients/bulk-assign-tags
```

**Body:**
```json
{
  "clientIds": [1, 2, 3],
  "tagIds": [1, 2]
}
```

**Response:**
```json
{
  "data": {
    "updated": 3
  }
}
```

### 1.11. Массовое удаление тегов
```
POST /clients/bulk-remove-tags
```

**Body:**
```json
{
  "clientIds": [1, 2, 3],
  "tagIds": [1, 2]
}
```

**Response:**
```json
{
  "data": {
    "updated": 3
  }
}
```

### 1.12. Массовое удаление клиентов
```
POST /clients/bulk-delete
```

**Body:**
```json
{
  "clientIds": [1, 2, 3]
}
```

**Response:**
```json
{
  "data": {
    "deleted": 3
  }
}
```

### 1.13. Импорт клиентов из файла
```
POST /clients/import
```

**Content-Type:** `multipart/form-data`

**Body:**
- `file` (File, required) - файл в формате Excel (.xlsx, .xls) или CSV

**Response:**
```json
{
  "data": {
    "imported": 150,
    "failed": 5,
    "errors": [
      {
        "row": 3,
        "error": "Некорректный формат телефона"
      }
    ]
  }
}
```

---

## 2. Группы клиентов (Client Groups)

### 2.1. Получить список групп
```
GET /client-groups
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, наименованию
- `status` (string, optional) - фильтр по статусу ("open" | "closed")
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "VIP",
      "discountPercent": 15,
      "discountApplication": "retail_only",
      "status": "open",
      "description": "Группа VIP клиентов",
      "clientsCount": 50,
      "createdAt": "2023-06-01T10:00:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 10,
    "totalPages": 1
  }
}
```

### 2.2. Получить группу по ID
```
GET /client-groups/:id
```

**Response:** (тот же формат, что и в списке, но один объект)

### 2.3. Получить статистику групп
```
GET /client-groups/statistics
```

**Response:**
```json
{
  "data": {
    "totalGroups": 10
  }
}
```

### 2.4. Создать группу
```
POST /client-groups
```

**Body:**
```json
{
  "name": "VIP",
  "discountPercent": 15,
  "discountApplication": "retail_only",
  "status": "open",
  "description": "Группа VIP клиентов"
}
```

**Response:** (созданная группа)

### 2.5. Обновить группу
```
PUT /client-groups/:id
```

**Body:** (те же поля, что и при создании)

**Response:** (обновленная группа)

### 2.6. Удалить группу
```
DELETE /client-groups/:id
```

**Response:**
```json
{
  "message": "Группа успешно удалена"
}
```

---

## 3. Теги клиентов (Client Tags)

### 3.1. Получить список тегов
```
GET /client-tags
```

**Query параметры:**
- `search` (string, optional) - поиск по ID, наименованию, типу
- `type` (string, optional) - фильтр по типу ("manual" | "auto")
- `status` (string, optional) - фильтр по статусу ("open" | "closed")
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "Постоянный клиент",
      "type": "manual",
      "status": "open",
      "description": "Клиент, который регулярно совершает покупки",
      "clientsCount": 120,
      "createdAt": "2023-06-01T10:00:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 25,
    "totalPages": 2
  }
}
```

### 3.2. Получить тег по ID
```
GET /client-tags/:id
```

**Response:** (тот же формат, что и в списке, но один объект)

### 3.3. Получить статистику тегов
```
GET /client-tags/statistics
```

**Response:**
```json
{
  "data": {
    "totalTags": 25
  }
}
```

### 3.4. Создать тег
```
POST /client-tags
```

**Body:**
```json
{
  "name": "Постоянный клиент",
  "type": "manual",
  "status": "open",
  "description": "Клиент, который регулярно совершает покупки"
}
```

**Response:** (созданный тег)

### 3.5. Обновить тег
```
PUT /client-tags/:id
```

**Body:** (те же поля, что и при создании)

**Response:** (обновленный тег)

### 3.6. Удалить тег
```
DELETE /client-tags/:id
```

**Response:**
```json
{
  "message": "Тег успешно удален"
}
```

---

## 4. Программа лояльности (Loyalty Program)

### 4.1. Получить программу лояльности
```
GET /loyalty-program
```

**Response:**
```json
{
  "data": {
    "id": 1,
    "type": "discount",
    "name": "Дисконтная система",
    "levels": [
      {
        "id": 1,
        "name": "Бронза",
        "purchaseAmount": 0,
        "discount": 5,
        "order": 1
      },
      {
        "id": 2,
        "name": "Серебро",
        "purchaseAmount": 50000,
        "discount": 10,
        "order": 2
      },
      {
        "id": 3,
        "name": "Золото",
        "purchaseAmount": 150000,
        "discount": 15,
        "order": 3
      }
    ],
    "createdAt": "2023-06-01T10:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
}
```

### 4.2. Обновить программу лояльности
```
PUT /loyalty-program
```

**Body:**
```json
{
  "type": "discount",
  "name": "Дисконтная система"
}
```

**Response:** (обновленная программа)

### 4.3. Создать уровень программы лояльности
```
POST /loyalty-program/levels
```

**Body:**
```json
{
  "name": "Платина",
  "purchaseAmount": 300000,
  "discount": 20,
  "order": 4
}
```

**Response:** (обновленная программа с новым уровнем)

### 4.4. Обновить уровень программы лояльности
```
PUT /loyalty-program/levels/:id
```

**Body:**
```json
{
  "name": "Платина",
  "purchaseAmount": 300000,
  "discount": 20,
  "order": 4
}
```

**Response:** (обновленная программа)

### 4.5. Удалить уровень программы лояльности
```
DELETE /loyalty-program/levels/:id
```

**Response:** (обновленная программа без удаленного уровня)

---

## 5. Долги клиентов (Client Debts)

### 5.1. Получить список долгов
```
GET /client-debts
```

**Query параметры:**
- `search` (string, optional) - поиск по клиенту, пользователю, магазину
- `status` (string, optional) - фильтр по статусу ("overdue" | "unpaid" | "paid" | "partial")
- `storeId` (number, optional) - фильтр по магазину
- `paymentType` (string, optional) - фильтр по типу оплаты
- `repaymentAmountFrom` (number, optional) - минимальная сумма погашения
- `repaymentAmountTo` (number, optional) - максимальная сумма погашения
- `clientId` (number, optional) - фильтр по клиенту
- `userId` (number, optional) - фильтр по пользователю
- `issueDateFrom` (string, optional) - дата начала выдачи (YYYY-MM-DD)
- `issueDateTo` (string, optional) - дата окончания выдачи (YYYY-MM-DD)
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "clientId": 1,
      "clientName": "Иванов Иван Иванович",
      "clientPhone": "+77001234567",
      "amount": 5000.00,
      "paidAmount": 2000.00,
      "remainingAmount": 3000.00,
      "status": "partial",
      "issueDate": "2024-01-10T10:00:00Z",
      "dueDate": "2024-02-10T10:00:00Z",
      "storeId": 1,
      "storeName": "Store Madiyar-accessories",
      "userId": 1,
      "userName": "user@example.com",
      "paymentType": "cash",
      "notes": "Долг за покупку товаров",
      "createdAt": "2024-01-10T10:00:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 50,
    "totalPages": 3
  }
}
```

### 5.2. Получить список погашений
```
GET /client-debts/repayments
```

**Query параметры:**
- `search` (string, optional) - поиск по клиенту, пользователю, магазину
- `storeId` (number, optional) - фильтр по магазину
- `paymentType` (string, optional) - фильтр по типу оплаты
- `repaymentAmountFrom` (number, optional) - минимальная сумма погашения
- `repaymentAmountTo` (number, optional) - максимальная сумма погашения
- `clientId` (number, optional) - фильтр по клиенту
- `userId` (number, optional) - фильтр по пользователю
- `repaymentDateFrom` (string, optional) - дата начала погашения (YYYY-MM-DD)
- `repaymentDateTo` (string, optional) - дата окончания погашения (YYYY-MM-DD)
- `page` (number, optional)
- `limit` (number, optional)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "debtId": 1,
      "amount": 2000.00,
      "paymentMethod": "cash",
      "paymentDate": "2024-01-15T10:30:00Z",
      "userId": 1,
      "userName": "user@example.com",
      "notes": "Частичное погашение",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "limit": 20,
    "total": 30,
    "totalPages": 2
  }
}
```

### 5.3. Получить статистику долгов
```
GET /client-debts/statistics
```

**Query параметры:**
- `issueDateFrom` (string, optional) - дата начала выдачи (YYYY-MM-DD)
- `issueDateTo` (string, optional) - дата окончания выдачи (YYYY-MM-DD)
- `repaymentDateFrom` (string, optional) - дата начала погашения (YYYY-MM-DD)
- `repaymentDateTo` (string, optional) - дата окончания погашения (YYYY-MM-DD)

**Response:**
```json
{
  "data": {
    "totalDebtAmount": 250000.00,
    "totalPaidAmount": 150000.00,
    "systemRepayments": 50000.00,
    "remainingDebt": 100000.00,
    "totalDebtors": 25,
    "paidDebts": 10,
    "unpaidDebts": 15,
    "overdueDebts": 5,
    "totalRepayments": 30
  }
}
```

### 5.4. Создать долг
```
POST /client-debts
```

**Body:**
```json
{
  "clientId": 1,
  "amount": 5000.00,
  "dueDate": "2024-02-10T10:00:00Z",
  "storeId": 1,
  "paymentType": "cash",
  "notes": "Долг за покупку товаров"
}
```

**Response:** (созданный долг)

### 5.5. Погасить долг
```
POST /client-debts/:id/repay
```

**Body:**
```json
{
  "amount": 2000.00,
  "paymentMethod": "cash",
  "notes": "Частичное погашение"
}
```

**Response:** (созданное погашение)

### 5.6. Массовое погашение долгов
```
POST /client-debts/bulk-repay
```

**Body:**
```json
{
  "debtIds": [1, 2, 3],
  "amount": 1000.00,
  "paymentMethod": "cash",
  "notes": "Массовое погашение"
}
```

**Response:**
```json
{
  "data": {
    "repaid": 3,
    "totalAmount": 3000.00
  }
}
```

### 5.7. Отправить SMS должникам
```
POST /client-debts/send-sms
```

**Body:**
```json
{
  "debtIds": [1, 2, 3],
  "message": "Напоминаем о задолженности. Пожалуйста, погасите долг."
}
```

**Response:**
```json
{
  "data": {
    "sent": 3,
    "failed": 0
  }
}
```

---

## 6. Поиск клиентов для долгов/SMS

### 6.1. Поиск клиентов с долгами
```
GET /clients/search/debts
```

**Query параметры:**
- `search` (string, optional) - поиск по имени, телефону
- `hasDebt` (boolean, optional) - только с долгами (по умолчанию true)

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "firstName": "Иван",
      "lastName": "Иванов",
      "fullName": "Иванов Иван Иванович",
      "phone": "+77001234567",
      "debtAmount": 5000.00
    }
  ]
}
```

---

## 7. Характеристики товаров для предпочтений клиентов

### 7.1. Получить список характеристик товаров
```
GET /products/characteristics
```

**Response:**
```json
{
  "data": [
    {
      "id": 1,
      "name": "category",
      "type": "category",
      "description": "Категория товара"
    },
    {
      "id": 2,
      "name": "size",
      "type": "size",
      "description": "Размер товара"
    },
    {
      "id": 3,
      "name": "color",
      "type": "color",
      "description": "Цвет товара"
    },
    {
      "id": 4,
      "name": "brand",
      "type": "brand",
      "description": "Бренд товара"
    },
    {
      "id": 5,
      "name": "material",
      "type": "material",
      "description": "Материал товара"
    },
    {
      "id": 6,
      "name": "price",
      "type": "price",
      "description": "Цена товара"
    }
  ]
}
```

**Типы характеристик:**
- `category` - Категория
- `size` - Размер
- `color` - Цвет
- `brand` - Бренд
- `material` - Материал
- `price` - Цена
- `other` - Другое

---

## 8. Настройки управления клиентами (Client Management Settings)

### 8.1. Получить настройки управления клиентами
```
GET /client-management/settings
```

**Response:**
```json
{
  "data": {
    "preferences": ["category", "size", "color", "brand"],
    "showRecommendations": true
  }
}
```

### 8.2. Обновить настройки управления клиентами
```
PUT /client-management/settings
```

**Body:**
```json
{
  "preferences": ["category", "size", "color", "brand", "price", "material"],
  "showRecommendations": false
}
```

**Response:** (обновленные настройки)

---

## Важные замечания

1. **Формат ответов:** Все успешные ответы должны возвращать данные в формате:
   ```json
   {
     "data": { ... }
   }
   ```
   или для списков:
   ```json
   {
     "data": [ ... ],
     "pagination": { ... }
   }
   ```

2. **Обработка ошибок:** При ошибке возвращайте:
   ```json
   {
     "error": "Описание ошибки",
     "message": "Детальное сообщение об ошибке"
   }
   ```
   с соответствующим HTTP статус-кодом (400, 401, 403, 404, 500 и т.д.).

3. **Авторизация:** Все эндпойнты требуют Bearer Token в заголовке:
   ```
   Authorization: Bearer <token>
   ```

4. **Даты:** Все даты должны быть в формате ISO 8601 (YYYY-MM-DDTHH:mm:ssZ).

5. **Валюты:** Все денежные суммы в KZT (казахстанский тенге).

6. **Пагинация:** По умолчанию `page = 1`, `limit = 20`. Максимальный `limit = 100`.

7. **Массовые операции:** Все массовые операции должны быть атомарными (либо все успешно, либо все откатываются).

8. **Импорт файлов:** Поддерживаемые форматы: Excel (.xlsx, .xls) и CSV. Файл должен содержать колонки: имя, фамилия, телефон, email, день рождения, пол и т.д.

---

## Статус эндпойнтов

### ⚠️ Требуют реализации:
- Все эндпойнты из этого документа

### ✅ Уже реализованы (если есть):
- (указать, если какие-то эндпойнты уже реализованы)

---

**Дата создания документа:** 2024-01-15  
**Версия:** 1.0

