<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Successful</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/support.css" rel="stylesheet">
</head>
<body class="support-page result-page">
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="result-card success-card text-center">
                    <div class="result-icon success"><i class="bi bi-check2-circle"></i></div>
                    <h1 class="fw-bold mt-3">Thank You for Your Support</h1>
                    <p class="text-muted mb-4">Your payment was successful and the transaction has been recorded.</p>

                    <div class="row g-3 text-start">
                        <div class="col-md-6">
                            <div class="mini-info-card">
                                <span>Support Type</span>
                                <strong><c:out value="${supportType != null ? supportType : 'N/A'}" /></strong>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mini-info-card">
                                <span>Amount Paid</span>
                                <strong>₹<c:out value="${amount != null ? amount : '0'}" /></strong>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mini-info-card">
                                <span>Transaction ID</span>
                                <strong><c:out value="${paymentId != null ? paymentId : 'N/A'}" /></strong>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="mini-info-card">
                                <span>Order ID</span>
                                <strong><c:out value="${orderId != null ? orderId : 'N/A'}" /></strong>
                            </div>
                        </div>
                    </div>

                    <div class="d-flex flex-column flex-sm-row gap-3 justify-content-center mt-4">
                        <a href="${pageContext.request.contextPath}/support" class="btn btn-support btn-lg">
                            <i class="bi bi-arrow-repeat me-2"></i>Support Again
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>