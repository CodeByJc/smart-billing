<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="common/header.jsp"><jsp:param name="pageTitle" value="Invoice History"/></jsp:include>
<jsp:include page="common/sidebar.jsp"/>
<div class="container-fluid py-4">
 <div class="d-flex justify-content-between align-items-center mb-4">
  <div><h1 class="page-title mb-1">Invoice History</h1><p class="text-muted mb-0">All generated invoices</p></div>
  <a href="${pageContext.request.contextPath}/billing" class="btn btn-primary"><i class="bi bi-plus-circle me-1"></i>New Invoice</a>
 </div>
 <div class="card dashboard-card">
  <div class="card-body p-0">
   <div class="table-responsive">
    <table class="table table-hover mb-0" id="invoiceHistoryTable">
     <thead><tr><th>#</th><th>Invoice Number</th><th>Customer</th><th>Subtotal</th><th>GST</th><th>Total</th><th>Payment</th><th>Date</th><th>Actions</th></tr></thead>
     <tbody>
      <c:choose>
       <c:when test="${empty invoices}">
        <tr><td colspan="9" class="text-center py-5 text-muted"><i class="bi bi-receipt fs-1 d-block mb-2"></i>No invoices found.</td></tr>
       </c:when>
       <c:otherwise>
        <c:forEach var="inv" items="${invoices}" varStatus="s">
         <tr>
          <td>${s.index+1}</td>
          <td><span class="fw-semibold text-primary">${inv.invoiceNumber}</span></td>
          <td>${inv.customerName}</td>
          <td><fmt:formatNumber value="${inv.subtotal}" type="currency" currencySymbol="&#8377;"/></td>
          <td><fmt:formatNumber value="${inv.gstAmount}" type="currency" currencySymbol="&#8377;"/></td>
          <td class="fw-bold"><fmt:formatNumber value="${inv.totalAmount}" type="currency" currencySymbol="&#8377;"/></td>
          <td><span class="badge ${inv.paymentType == 'Cash' ? 'bg-success' : 'bg-info'}">${inv.paymentType}</span></td>
          <td><fmt:formatDate value="${inv.createdAt}" pattern="dd MMM yyyy"/></td>
          <td>
           <div class="btn-group btn-group-sm">
            <a href="${pageContext.request.contextPath}/invoices/view/${inv.id}" class="btn btn-outline-primary" title="View"><i class="bi bi-eye"></i></a>
            <a href="${pageContext.request.contextPath}/invoices/pdf/${inv.id}" class="btn btn-outline-danger" title="PDF"><i class="bi bi-file-pdf"></i></a>
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
<jsp:include page="common/footer.jsp"/>
