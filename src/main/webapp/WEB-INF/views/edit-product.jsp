<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="common/header.jsp"><jsp:param name="pageTitle" value="Edit Product"/></jsp:include>
<jsp:include page="common/sidebar.jsp"/>
<div class="container-fluid py-4">
 <div class="row justify-content-center"><div class="col-lg-8">
  <div class="d-flex align-items-center mb-4">
   <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary me-3"><i class="bi bi-arrow-left"></i></a>
   <div><h1 class="page-title mb-0">Edit Product</h1><p class="text-muted mb-0">Update product details</p></div>
  </div>
  <c:if test="${not empty error}"><div class="alert alert-danger alert-dismissible fade show"><i class="bi bi-exclamation-triangle me-2"></i>${error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div></c:if>
  <div class="card dashboard-card"><div class="card-body p-4">
   <form action="${pageContext.request.contextPath}/products/edit/${product.id}" method="post" onsubmit="return validateProductForm()">
    <div class="mb-3"><label for="name" class="form-label fw-semibold">Product Name <span class="text-danger">*</span></label>
     <input type="text" class="form-control" id="name" name="name" value="${product.name}" required></div>
    <div class="mb-3"><label for="description" class="form-label fw-semibold">Description</label>
     <textarea class="form-control" id="description" name="description" rows="3">${product.description}</textarea></div>
    <div class="row g-3 mb-3">
     <div class="col-md-4"><label for="price" class="form-label fw-semibold">Price &#8377; <span class="text-danger">*</span></label>
      <input type="number" class="form-control" id="price" name="price" value="${product.price}" step="0.01" min="0.01" required></div>
     <div class="col-md-4"><label for="gstPercentage" class="form-label fw-semibold">GST % <span class="text-danger">*</span></label>
      <div class="input-group"><input type="number" class="form-control" id="gstPercentage" name="gstPercentage" value="${product.gstPercentage}" step="0.01" min="0" max="100" required><span class="input-group-text">%</span></div></div>
     <div class="col-md-4"><label for="stockQuantity" class="form-label fw-semibold">Stock <span class="text-danger">*</span></label>
      <input type="number" class="form-control" id="stockQuantity" name="stockQuantity" value="${product.stockQuantity}" min="0" required></div>
    </div>
    <hr class="my-4">
    <div class="d-flex justify-content-end gap-2">
     <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary"><i class="bi bi-x-circle me-1"></i>Cancel</a>
     <button type="submit" class="btn btn-primary"><i class="bi bi-check-circle me-1"></i>Update Product</button>
    </div>
   </form>
  </div></div>
 </div></div>
</div>
<script>
function validateProductForm(){
 var n=document.getElementById('name').value.trim();
 var p=parseFloat(document.getElementById('price').value);
 var g=parseFloat(document.getElementById('gstPercentage').value);
 var s=parseInt(document.getElementById('stockQuantity').value);
 if(!n){alert('Product name is required');return false;}
 if(isNaN(p)||p<=0){alert('Price must be > 0');return false;}
 if(isNaN(g)||g<0||g>100){alert('GST must be 0-100');return false;}
 if(isNaN(s)||s<0){alert('Stock cannot be negative');return false;}
 return true;
}
</script>
<jsp:include page="common/footer.jsp"/>
