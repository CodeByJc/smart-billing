<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!-- Sidebar Navigation -->
<div class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <h5 class="mb-0"><i class="bi bi-speedometer2 me-2"></i>Navigation</h5>
    </div>
    <nav class="sidebar-nav">
        <a href="${pageContext.request.contextPath}/dashboard"
           class="sidebar-link ${activePage == 'dashboard' ? 'active' : ''}">
            <i class="bi bi-grid-1x2-fill"></i>
            <span>Dashboard</span>
        </a>
        <a href="${pageContext.request.contextPath}/products"
           class="sidebar-link ${activePage == 'products' ? 'active' : ''}">
            <i class="bi bi-box-seam-fill"></i>
            <span>Products</span>
        </a>
        <a href="${pageContext.request.contextPath}/billing"
           class="sidebar-link ${activePage == 'billing' ? 'active' : ''}">
            <i class="bi bi-cart-plus-fill"></i>
            <span>New Invoice</span>
        </a>
        <a href="${pageContext.request.contextPath}/invoices/history"
           class="sidebar-link ${activePage == 'invoices' ? 'active' : ''}">
            <i class="bi bi-clock-history"></i>
            <span>Invoice History</span>
        </a>
    </nav>
    <div class="sidebar-footer">
        <small class="text-muted">&copy; 2026 Smart Billing</small>
    </div>
</div>

<!-- Main Content Wrapper -->
<div class="main-content" id="mainContent">
