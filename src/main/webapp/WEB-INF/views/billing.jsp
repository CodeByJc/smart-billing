<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="common/header.jsp"><jsp:param name="pageTitle" value="Billing"/></jsp:include>
<jsp:include page="common/sidebar.jsp"/>
<div class="container-fluid py-4">
 <div class="d-flex justify-content-between align-items-center mb-4">
  <div><h1 class="page-title mb-1">Create Invoice</h1><p class="text-muted mb-0">Add products and generate invoice</p></div>
 </div>
 <c:if test="${not empty success}"><div class="alert alert-success alert-dismissible fade show"><i class="bi bi-check-circle me-2"></i>${success}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div></c:if>
 <c:if test="${not empty error}"><div class="alert alert-danger alert-dismissible fade show"><i class="bi bi-exclamation-triangle me-2"></i>${error}<button type="button" class="btn-close" data-bs-dismiss="alert"></button></div></c:if>

 <form action="${pageContext.request.contextPath}/billing/create" method="post" id="billingForm" onsubmit="return validateBillingForm()">
  <div class="row g-4">
   <div class="col-lg-8">
    <div class="card dashboard-card mb-4">
     <div class="card-header"><h5 class="mb-0"><i class="bi bi-person me-2"></i>Customer Details</h5></div>
     <div class="card-body">
      <div class="row g-3">
       <div class="col-md-6">
        <label for="customerName" class="form-label fw-semibold">Customer Name <span class="text-danger">*</span></label>
        <input type="text" class="form-control" id="customerName" name="customerName" placeholder="Enter customer name" required>
       </div>
       <div class="col-md-6">
        <label for="paymentType" class="form-label fw-semibold">Payment Type <span class="text-danger">*</span></label>
        <select class="form-select" id="paymentType" name="paymentType" required>
         <option value="Cash">Cash</option><option value="Online">Online</option>
        </select>
       </div>
      </div>
     </div>
    </div>
    <div class="card dashboard-card">
     <div class="card-header d-flex justify-content-between align-items-center">
      <h5 class="mb-0"><i class="bi bi-cart-plus me-2"></i>Products</h5>
      <button type="button" class="btn btn-primary btn-sm" id="addRowBtn" onclick="addProductRow()"><i class="bi bi-plus-circle me-1"></i>Add Product</button>
     </div>
     <div class="card-body p-0">
      <div class="table-responsive">
       <table class="table table-hover mb-0" id="billingTable">
        <thead><tr><th>#</th><th>Product</th><th>Price</th><th>GST %</th><th>Stock</th><th>Qty</th><th>GST Amt</th><th>Total</th><th></th></tr></thead>
        <tbody id="billingTableBody">
         <tr class="product-row" id="row_0">
          <td>1</td>
          <td><select class="form-select product-select" name="productId" data-row="0" onchange="onProductSelect(this)" required>
           <option value="">-- Select Product --</option>
           <c:forEach var="p" items="${products}"><option value="${p.id}" data-price="${p.price}" data-gst="${p.gstPercentage}" data-stock="${p.stockQuantity}" data-name="${p.name}">${p.name}</option></c:forEach>
          </select></td>
          <td class="product-price">-</td><td class="product-gst">-</td><td class="product-stock">-</td>
          <td><input type="number" class="form-control form-control-sm quantity-input" name="quantity" min="1" value="1" data-row="0" onchange="calculateRow(0)" onkeyup="calculateRow(0)" style="width:80px" required></td>
          <td class="row-gst-amount">-</td><td class="row-total fw-semibold">-</td>
          <td><button type="button" class="btn btn-sm btn-outline-danger" onclick="removeRow(0)" title="Remove"><i class="bi bi-x-lg"></i></button></td>
         </tr>
        </tbody>
       </table>
      </div>
     </div>
    </div>
   </div>
   <div class="col-lg-4">
    <div class="card dashboard-card billing-summary-card">
     <div class="card-header"><h5 class="mb-0"><i class="bi bi-calculator me-2"></i>Summary</h5></div>
     <div class="card-body">
      <div class="d-flex justify-content-between mb-2"><span>Subtotal</span><span id="summarySubtotal" class="fw-semibold">&#8377;0.00</span></div>
      <div class="d-flex justify-content-between mb-2"><span>GST Amount</span><span id="summaryGst" class="fw-semibold">&#8377;0.00</span></div>
      <hr>
      <div class="d-flex justify-content-between mb-3"><span class="fs-5 fw-bold">Grand Total</span><span id="summaryTotal" class="fs-5 fw-bold text-success">&#8377;0.00</span></div>
      <button type="submit" class="btn btn-success btn-lg w-100" id="generateInvoiceBtn"><i class="bi bi-receipt me-2"></i>Generate Invoice</button>
     </div>
    </div>
   </div>
  </div>
 </form>
</div>
<script>
var rowCounter = 1;
var productsData = {};
<c:forEach var="p" items="${products}">
productsData[${p.id}] = {price:${p.price},gst:${p.gstPercentage},stock:${p.stockQuantity},name:'${p.name}'};
</c:forEach>

function addProductRow(){
 var tbody = document.getElementById('billingTableBody');
 var idx = rowCounter++;
 var options = '<option value="">-- Select Product --</option>';
 for(var id in productsData){
  var p = productsData[id];
  options += '<option value="'+id+'" data-price="'+p.price+'" data-gst="'+p.gst+'" data-stock="'+p.stock+'" data-name="'+p.name+'">'+p.name+'</option>';
 }
 var tr = document.createElement('tr');
 tr.className='product-row'; tr.id='row_'+idx;
 tr.innerHTML='<td>'+(tbody.children.length+1)+'</td>'
  +'<td><select class="form-select product-select" name="productId" data-row="'+idx+'" onchange="onProductSelect(this)" required>'+options+'</select></td>'
  +'<td class="product-price">-</td><td class="product-gst">-</td><td class="product-stock">-</td>'
  +'<td><input type="number" class="form-control form-control-sm quantity-input" name="quantity" min="1" value="1" data-row="'+idx+'" onchange="calculateRow('+idx+')" onkeyup="calculateRow('+idx+')" style="width:80px" required></td>'
  +'<td class="row-gst-amount">-</td><td class="row-total fw-semibold">-</td>'
  +'<td><button type="button" class="btn btn-sm btn-outline-danger" onclick="removeRow('+idx+')" title="Remove"><i class="bi bi-x-lg"></i></button></td>';
 tbody.appendChild(tr);
 renumberRows();
}

function onProductSelect(sel){
 var row = sel.closest('tr');
 var opt = sel.options[sel.selectedIndex];
 if(!opt.value){row.querySelector('.product-price').textContent='-';row.querySelector('.product-gst').textContent='-';row.querySelector('.product-stock').textContent='-';row.querySelector('.row-gst-amount').textContent='-';row.querySelector('.row-total').textContent='-';updateSummary();return;}
 row.querySelector('.product-price').textContent='\u20B9'+parseFloat(opt.dataset.price).toFixed(2);
 row.querySelector('.product-gst').textContent=opt.dataset.gst+'%';
 row.querySelector('.product-stock').textContent=opt.dataset.stock;
 var ridx = parseInt(sel.dataset.row);
 calculateRow(ridx);
}

function calculateRow(idx){
 var row = document.getElementById('row_'+idx);
 if(!row) return;
 var sel = row.querySelector('.product-select');
 var opt = sel.options[sel.selectedIndex];
 if(!opt||!opt.value) return;
 var price = parseFloat(opt.dataset.price);
 var gst = parseFloat(opt.dataset.gst);
 var qty = parseInt(row.querySelector('.quantity-input').value)||0;
 var base = price*qty;
 var gstAmt = base*gst/100;
 var total = base+gstAmt;
 row.querySelector('.row-gst-amount').textContent='\u20B9'+gstAmt.toFixed(2);
 row.querySelector('.row-total').textContent='\u20B9'+total.toFixed(2);
 updateSummary();
}

function updateSummary(){
 var rows = document.querySelectorAll('.product-row');
 var sub=0,gst=0;
 rows.forEach(function(row){
  var sel = row.querySelector('.product-select');
  var opt = sel.options[sel.selectedIndex];
  if(!opt||!opt.value) return;
  var p=parseFloat(opt.dataset.price);
  var g=parseFloat(opt.dataset.gst);
  var q=parseInt(row.querySelector('.quantity-input').value)||0;
  var b=p*q; sub+=b; gst+=b*g/100;
 });
 document.getElementById('summarySubtotal').textContent='\u20B9'+sub.toFixed(2);
 document.getElementById('summaryGst').textContent='\u20B9'+gst.toFixed(2);
 document.getElementById('summaryTotal').textContent='\u20B9'+(sub+gst).toFixed(2);
}

function removeRow(idx){
 var row=document.getElementById('row_'+idx);
 if(document.querySelectorAll('.product-row').length<=1){alert('At least one product is required');return;}
 if(row)row.remove(); renumberRows(); updateSummary();
}

function renumberRows(){
 var rows=document.querySelectorAll('#billingTableBody .product-row');
 rows.forEach(function(r,i){r.querySelector('td:first-child').textContent=i+1;});
}

function validateBillingForm(){
 var name=document.getElementById('customerName').value.trim();
 if(!name){alert('Customer name is required');return false;}
 var rows=document.querySelectorAll('.product-row');
 for(var i=0;i<rows.length;i++){
  var sel=rows[i].querySelector('.product-select');
  if(!sel.value){alert('Please select a product in row '+(i+1));return false;}
  var qty=parseInt(rows[i].querySelector('.quantity-input').value);
  var stock=parseInt(sel.options[sel.selectedIndex].dataset.stock);
  if(qty<=0){alert('Quantity must be > 0 in row '+(i+1));return false;}
  if(qty>stock){alert('Insufficient stock for '+sel.options[sel.selectedIndex].dataset.name+'. Available: '+stock);return false;}
 }
 return true;
}
</script>
<jsp:include page="common/footer.jsp"/>
