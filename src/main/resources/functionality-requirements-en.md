# Wiki Inventory & POS System - Complete Functionality Requirements

## Overview
Wiki is a multi-tenant inventory management and point-of-sale system for small and medium retail businesses in Kazakhstan. This document outlines all functionality required for the frontend implementation.

---

## 1. Authentication & User Management

### 1.1 Login Page
- Email and password input fields
- "Remember me" checkbox (optional)
- "Forgot password" link (future feature)
- Login button with loading state
- Error message display for invalid credentials
- Redirect to dashboard on successful login

### 1.2 Registration Page
- Company name input (generates tenant ID automatically)
- Owner email input
- Password input with strength indicator
- Full name input
- Role selection (Owner/Admin/Seller)
- Terms and conditions checkbox
- Registration button
- Redirect to login after successful registration

### 1.3 Token Management
- Store JWT access token in memory/localStorage
- Store refresh token securely
- Automatic token refresh before expiration
- Logout functionality (clear tokens)
- Session timeout handling

### 1.4 User Profile (Owner/Admin only)
- View current user information
- Update full name
- Change password (with current password verification)
- View assigned role and tenant information

---

## 2. Dashboard & Analytics

### 2.1 Main Dashboard
- **Today's Revenue Card**: Large display of daily revenue in KZT
- **Today's Sales Count**: Number of completed sales
- **Quick Stats Cards**: 
  - Total products
  - Low stock alerts (products below threshold)
  - Pending actions
- **Recent Sales Table**: Last 10 sales with date, amount, items count
- **Revenue Chart**: Daily revenue trend (last 7 days)
- Date picker to view different days
- Refresh button to update data

### 2.2 Analytics Page
- **Date Range Selector**: Start and end date pickers
- **Revenue Analytics**:
  - Total revenue for selected period
  - Average daily revenue
  - Revenue by day chart (line/bar chart)
- **Sales Analytics**:
  - Total number of sales
  - Average sale amount
  - Sales count by day chart
- **Product Performance**:
  - Top 10 selling products
  - Products by revenue
- **Export Options**: PDF/Excel export (future feature)

---

## 3. Product Management

### 3.1 Products List Page
- **Search Bar**: Search by product name or SKU
- **Filter Options**:
  - Filter by category
  - Filter by stock status (in stock, low stock, out of stock)
  - Sort by name, price, stock quantity
- **Product Grid/Table View**:
  - Product image placeholder
  - Product name
  - SKU
  - Price (KZT)
  - Stock quantity with color coding (green/yellow/red)
  - Category badge
  - Actions: Edit, Delete, View Details
- **Pagination**: Page size selector and page navigation
- **Bulk Actions**: Select multiple products for bulk operations (future)

### 3.2 Create/Edit Product Page
- **Basic Information**:
  - Product name (required)
  - SKU (required, unique per tenant)
  - Price in KZT (required, decimal)
  - Category dropdown selector
- **Stock Information**:
  - Initial stock quantity (default: 0)
  - Low stock threshold (optional)
- **Additional Fields** (future):
  - Product description
  - Product image upload
  - Barcode field
- **Form Validation**: Real-time validation with error messages
- **Save/Cancel Buttons**: Save creates/updates, Cancel returns to list

### 3.3 Product Details Page
- Full product information display
- Stock history timeline
- Recent sales including this product
- Edit and Delete buttons (if user has permission)
- Quick stock adjustment button

---

## 4. Category Management

### 4.1 Categories List Page
- List of all categories
- Category name
- Product count per category
- Edit and Delete actions
- Create new category button

### 4.2 Create/Edit Category Page
- Category name input (required, unique per tenant)
- Save/Cancel buttons
- Validation for duplicate names

---

## 5. Point of Sale (POS)

### 5.1 POS Terminal Interface
- **Product Search Bar**: Real-time search as you type
- **Product Selection Area**:
  - Grid/list of products with images
  - Quick add buttons (+1, +5, +10)
  - Stock availability indicator
- **Cart/Sale Items Panel**:
  - List of selected items with:
    - Product name
    - Quantity (with +/- buttons)
    - Unit price
    - Total price per item
    - Remove item button
  - Subtotal display
  - Tax calculation (if applicable, future)
  - **Grand Total** (large, prominent)
- **Payment Section**:
  - Cash amount input
  - Change calculation display
  - Payment method selector (Cash only for MVP)
  - Complete Sale button
- **Quick Actions**:
  - Clear cart button
  - Hold sale button (future)
  - Discount button (future)

### 5.2 Receipt Preview/Print
- Receipt template with:
  - Company name and address
  - Sale date and time
  - Sale number/ID
  - Itemized list with quantities and prices
  - Subtotal and total
  - Payment method and amount
  - Change given
- Print button (opens print dialog)
- Email receipt button (future)
- Save as PDF button (future)

---

## 6. Sales & Returns

### 6.1 Sales History Page
- **Date Range Filter**: Today, This Week, This Month, Custom range
- **Sales List Table**:
  - Sale ID/Number
  - Date and time
  - Total amount
  - Number of items
  - Cashier name
  - Sale type (Sale/Return)
  - Actions: View Details, Process Return
- **Search Functionality**: Search by sale ID or customer (future)
- **Export Options**: Export to Excel/PDF

### 6.2 Sale Details Page
- Complete sale information
- Itemized list of products sold
- Payment details
- Return button (if not already a return)
- Print receipt button

### 6.3 Return Processing
- Select original sale to return
- Display original sale items
- Select items to return (full or partial)
- Reason for return input (optional)
- Process return button
- Confirmation dialog before processing
- Automatic stock restoration

---

## 7. Stock Management

### 7.1 Stock Overview Page
- **Stock Status Cards**:
  - Total products
  - In stock items
  - Low stock items (below threshold)
  - Out of stock items
- **Low Stock Alerts**: List of products needing restocking
- **Stock Movements Table**: Recent stock transactions
- **Quick Actions**: Bulk stock update (future)

### 7.2 Stock Receipt Page
- Product selector (searchable dropdown)
- Quantity input
- Reason/Notes field (optional)
- Supplier information (future)
- Record receipt button
- Confirmation and success message

### 7.3 Stock Write-off Page
- Product selector
- Quantity input (with current stock display)
- Reason selector (Damage, Loss, Theft, Expired, Other)
- Additional notes field
- Record write-off button
- Warning if quantity exceeds available stock

### 7.4 Product Stock History
- Timeline view of all stock transactions
- Filter by transaction type (Receipt/Write-off/Sale)
- Date range filter
- Export history option

---

## 8. User Management (Owner/Admin only)

### 8.1 Users List Page
- Table of all users in the company
- User email, full name, role
- Status (Active/Inactive)
- Last login date
- Actions: Edit, Deactivate, Delete

### 8.2 Create/Edit User Page
- Email input
- Full name input
- Role selector (Owner/Admin/Seller)
- Password input (for new users)
- Send invitation email (future)
- Save/Cancel buttons

---

## 9. Settings (Owner only)

### 9.1 Company Settings
- Company name (editable)
- Company address
- Contact information
- Tax ID/VAT number (future)
- Logo upload (future)

### 9.2 System Settings
- Currency settings (KZT)
- Date/time format
- Receipt template customization (future)
- Low stock threshold (global default)
- Backup settings

---

## 10. Navigation & Layout

### 10.1 Sidebar Navigation
- Dashboard icon and link
- Products icon and link
- Categories icon and link
- POS Terminal icon and link (prominent)
- Sales icon and link
- Stock Management icon and link
- Analytics icon and link
- Users icon and link (Owner/Admin only)
- Settings icon and link (Owner only)
- Logout button (bottom)

### 10.2 Top Bar
- Company name/logo
- Current user name and role
- Notifications icon (future)
- User profile dropdown menu

### 10.3 Responsive Design
- Mobile-friendly layout
- Collapsible sidebar on mobile
- Touch-friendly buttons for POS
- Tablet optimization

---

## 11. Role-Based Access Control

### 11.1 Owner Permissions
- Full access to all features
- User management
- Company settings
- All reports and analytics

### 11.2 Admin Permissions
- Product management
- Sales processing
- Stock management
- View analytics
- No access to user management or settings

### 11.3 Seller Permissions
- POS terminal access
- Process sales
- View product catalog
- View stock levels
- No access to product editing, stock adjustments, or analytics

---

## 12. Error Handling & User Feedback

### 12.1 Error Messages
- Clear, user-friendly error messages
- Validation errors displayed inline
- Network error handling
- Session expired notifications

### 12.2 Success Messages
- Confirmation messages for successful actions
- Auto-dismiss after 3-5 seconds
- Undo option for critical actions (future)

### 12.3 Loading States
- Loading spinners for async operations
- Skeleton screens for data loading
- Disabled buttons during processing

---

## Technical Notes for Frontend

- All API endpoints are prefixed with `/api`
- Authentication: Bearer token in Authorization header
- Date format: ISO 8601 (YYYY-MM-DD)
- Currency: KZT (Kazakhstani Tenge)
- Language support: Prepare for Kazakh/Russian/English (future)
- All monetary values use BigDecimal precision (2 decimal places)

---

## API Endpoints Reference

### Authentication
- `POST /api/auth/register` - Register new user and company
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token

### Categories
- `POST /api/categories` - Create category
- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Products
- `POST /api/products` - Create product
- `GET /api/products` - Get all products (with optional `search` and `categoryId` query params)
- `GET /api/products/{id}` - Get product by ID
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `GET /api/products/sku/{sku}/exists` - Check if SKU exists

### Sales
- `POST /api/sales` - Create sale
- `POST /api/sales/{id}/return` - Process return
- `GET /api/sales` - Get all sales (with optional `startDate` and `endDate` query params)
- `GET /api/sales/{id}` - Get sale by ID

### Stock
- `POST /api/stock/receipt` - Record stock receipt
- `POST /api/stock/write-off` - Record stock write-off
- `GET /api/stock/product/{productId}/history` - Get product stock history
- `GET /api/stock/product/{productId}/current` - Get current stock level

### Analytics
- `GET /api/analytics/daily/revenue` - Get daily revenue (optional `date` query param)
- `GET /api/analytics/daily/sales-count` - Get daily sales count (optional `date` query param)
- `GET /api/analytics/revenue` - Get revenue by date range (`startDate`, `endDate` required)
- `GET /api/analytics/sales-count` - Get sales count by date range (`startDate`, `endDate` required)
- `GET /api/analytics/dashboard` - Get complete dashboard data (optional `date` query param)

---
