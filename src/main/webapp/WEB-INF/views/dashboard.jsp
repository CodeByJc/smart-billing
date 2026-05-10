<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Dashboard"/>
</jsp:include>
<jsp:include page="common/sidebar.jsp"/>

<div class="container-fluid py-4">
    <!-- Page Title -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="page-title mb-1">Dashboard</h1>
            <p class="text-muted mb-0">Welcome back, ${sessionScope.username}!</p>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/billing" class="btn btn-primary">
                <i class="bi bi-plus-circle me-2"></i>New Invoice
            </a>
        </div>
    </div>

    <!-- Stats Cards -->
    <div class="row g-4 mb-4">
        <!-- Total Products -->
        <div class="col-xl-3 col-md-6">
            <div class="stat-card stat-card-primary">
                <div class="stat-card-body">
                    <div class="stat-icon">
                        <i class="bi bi-box-seam-fill"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="stat-label">Total Products</h6>
                        <h2 class="stat-value">${stats.totalProducts}</h2>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/products" class="stat-card-footer">
                    View All <i class="bi bi-arrow-right ms-1"></i>
                </a>
            </div>
        </div>

        <!-- Total Sales -->
        <div class="col-xl-3 col-md-6">
            <div class="stat-card stat-card-success">
                <div class="stat-card-body">
                    <div class="stat-icon">
                        <i class="bi bi-currency-rupee"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="stat-label">Total Sales</h6>
                        <h2 class="stat-value">
                            <fmt:formatNumber value="${stats.totalSales}" type="currency"
                                              currencySymbol="&#8377;" maxFractionDigits="0"/>
                        </h2>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/invoices/history" class="stat-card-footer">
                    View History <i class="bi bi-arrow-right ms-1"></i>
                </a>
            </div>
        </div>

        <!-- Total Invoices -->
        <div class="col-xl-3 col-md-6">
            <div class="stat-card stat-card-info">
                <div class="stat-card-body">
                    <div class="stat-icon">
                        <i class="bi bi-receipt"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="stat-label">Total Invoices</h6>
                        <h2 class="stat-value">${stats.totalInvoices}</h2>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/invoices/history" class="stat-card-footer">
                    View All <i class="bi bi-arrow-right ms-1"></i>
                </a>
            </div>
        </div>

        <!-- Low Stock Alert -->
        <div class="col-xl-3 col-md-6">
            <div class="stat-card stat-card-warning">
                <div class="stat-card-body">
                    <div class="stat-icon">
                        <i class="bi bi-exclamation-triangle-fill"></i>
                    </div>
                    <div class="stat-info">
                        <h6 class="stat-label">Low Stock Items</h6>
                        <h2 class="stat-value">${stats.lowStockCount}</h2>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/products?search=low" class="stat-card-footer">
                    View Items <i class="bi bi-arrow-right ms-1"></i>
                </a>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <!-- Recent Invoices -->
        <div class="col-lg-8">
            <div class="card dashboard-card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-clock-history me-2"></i>Recent Invoices</h5>
                    <a href="${pageContext.request.contextPath}/invoices/history" class="btn btn-sm btn-outline-primary">
                        View All
                    </a>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0" id="recentInvoicesTable">
                            <thead>
                                <tr>
                                    <th>Invoice #</th>
                                    <th>Customer</th>
                                    <th>Amount</th>
                                    <th>Payment</th>
                                    <th>Date</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty stats.recentInvoices}">
                                        <tr>
                                            <td colspan="6" class="text-center py-4 text-muted">
                                                <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                                                No invoices yet. Create your first invoice!
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="inv" items="${stats.recentInvoices}">
                                            <tr>
                                                <td><span class="fw-semibold text-primary">${inv.invoiceNumber}</span></td>
                                                <td>${inv.customerName}</td>
                                                <td class="fw-semibold">
                                                    <fmt:formatNumber value="${inv.totalAmount}" type="currency"
                                                                      currencySymbol="&#8377;"/>
                                                </td>
                                                <td>
                                                    <span class="badge ${inv.paymentType == 'Cash' ? 'bg-success' : 'bg-info'}">
                                                        ${inv.paymentType}
                                                    </span>
                                                </td>
                                                <td>
                                                    <fmt:formatDate value="${inv.createdAt}" pattern="dd MMM yyyy"/>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/invoices/view/${inv.id}"
                                                       class="btn btn-sm btn-outline-primary">
                                                        <i class="bi bi-eye"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Low Stock Alerts -->
        <div class="col-lg-4">
            <div class="card dashboard-card">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-exclamation-triangle me-2 text-warning"></i>Low Stock Alerts</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${empty stats.lowStockProducts}">
                            <div class="text-center py-4 text-muted">
                                <i class="bi bi-check-circle fs-1 d-block mb-2 text-success"></i>
                                All products are well stocked!
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="list-group list-group-flush">
                                <c:forEach var="product" items="${stats.lowStockProducts}">
                                    <div class="list-group-item d-flex justify-content-between align-items-center px-0">
                                        <div>
                                            <h6 class="mb-0">${product.name}</h6>
                                            <small class="text-muted">
                                                <fmt:formatNumber value="${product.price}" type="currency"
                                                                  currencySymbol="&#8377;"/>
                                            </small>
                                        </div>
                                        <span class="badge bg-danger rounded-pill">${product.stockQuantity} left</span>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="common/footer.jsp"/>
