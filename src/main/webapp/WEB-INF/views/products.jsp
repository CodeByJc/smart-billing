<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="common/header.jsp">
    <jsp:param name="pageTitle" value="Products"/>
</jsp:include>
<jsp:include page="common/sidebar.jsp"/>

<div class="container-fluid py-4">
    <!-- Page Title & Actions -->
    <div class="d-flex flex-wrap justify-content-between align-items-center mb-4">
        <div>
            <h1 class="page-title mb-1">Product Management</h1>
            <p class="text-muted mb-0">Manage your product inventory</p>
        </div>
        <div class="d-flex gap-2 mt-2 mt-md-0">
            <form class="d-flex" action="${pageContext.request.contextPath}/products" method="get" id="searchForm">
                <div class="input-group">
                    <input type="text" class="form-control" name="search" placeholder="Search products..."
                           value="${search}" id="productSearch">
                    <button class="btn btn-outline-primary" type="submit">
                        <i class="bi bi-search"></i>
                    </button>
                </div>
            </form>
            <a href="${pageContext.request.contextPath}/products/add" class="btn btn-primary">
                <i class="bi bi-plus-circle me-1"></i>Add Product
            </a>
        </div>
    </div>

    <!-- Flash Messages -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert" id="successAlert">
            <i class="bi bi-check-circle me-2"></i>${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert" id="errorAlert">
            <i class="bi bi-exclamation-triangle me-2"></i>${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Products Table -->
    <div class="card dashboard-card">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-hover mb-0" id="productsTable">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Product Name</th>
                            <th>Description</th>
                            <th>Price (&#8377;)</th>
                            <th>GST %</th>
                            <th>Stock</th>
                            <th>Created</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty products}">
                                <tr>
                                    <td colspan="8" class="text-center py-5 text-muted">
                                        <i class="bi bi-box-seam fs-1 d-block mb-2"></i>
                                        No products found.
                                        <a href="${pageContext.request.contextPath}/products/add">Add your first product</a>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="product" items="${products}" varStatus="status">
                                    <tr>
                                        <td>${status.index + 1}</td>
                                        <td class="fw-semibold">${product.name}</td>
                                        <td class="text-muted">
                                            <c:choose>
                                                <c:when test="${product.description != null && product.description.length() > 50}">
                                                    ${product.description.substring(0, 50)}...
                                                </c:when>
                                                <c:otherwise>${product.description}</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${product.price}" type="currency"
                                                              currencySymbol="&#8377;"/>
                                        </td>
                                        <td>${product.gstPercentage}%</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${product.stockQuantity < 10}">
                                                    <span class="badge bg-danger">${product.stockQuantity}</span>
                                                </c:when>
                                                <c:when test="${product.stockQuantity < 25}">
                                                    <span class="badge bg-warning text-dark">${product.stockQuantity}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-success">${product.stockQuantity}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${product.createdAt}" pattern="dd MMM yyyy"/>
                                        </td>
                                        <td>
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/products/edit/${product.id}"
                                                   class="btn btn-outline-primary" title="Edit">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <button class="btn btn-outline-danger" title="Delete"
                                                        onclick="confirmDelete(${product.id}, '${product.name}')">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </div>
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

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header border-0">
                <h5 class="modal-title" id="deleteModalLabel">
                    <i class="bi bi-exclamation-triangle text-danger me-2"></i>Confirm Delete
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                Are you sure you want to delete <strong id="deleteProductName"></strong>?
                This action cannot be undone.
            </div>
            <div class="modal-footer border-0">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <a href="#" id="deleteConfirmBtn" class="btn btn-danger">
                    <i class="bi bi-trash me-1"></i>Delete
                </a>
            </div>
        </div>
    </div>
</div>

<script>
    function confirmDelete(id, name) {
        document.getElementById('deleteProductName').textContent = name;
        document.getElementById('deleteConfirmBtn').href =
            '${pageContext.request.contextPath}/products/delete/' + id;
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>

<jsp:include page="common/footer.jsp"/>
