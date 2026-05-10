<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Support Developer page">
    <title>Support Developer</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/support.css" rel="stylesheet">
</head>
<body class="support-page">
    <div class="support-hero">
        <div class="support-orb support-orb-1"></div>
        <div class="support-orb support-orb-2"></div>
        <div class="container py-5 position-relative">
            <div class="row justify-content-center">
                <div class="col-12 col-xl-10">
                    <div class="support-panel">
                        <div class="support-header text-center mb-4">
                            <span class="support-badge"><i class="bi bi-heart-fill me-1"></i>Public Support Page</span>
                            <h1 class="display-5 fw-bold mt-3 mb-2">Support Developer</h1>
                            <p class="lead mb-0">1Choose a support type, select an amount, and complete payment securely with Razorpay.</p>
                        </div>

                        <div id="supportSuccessAlert" class="alert alert-success d-none" role="alert"></div>
                        <div id="supportFailureAlert" class="alert alert-danger d-none" role="alert"></div>

                        <div class="row g-4">
                            <div class="col-12 col-lg-6">
                                <div class="support-section-card h-100">
                                    <div class="section-title mb-3">1. Choose Support Type</div>
                                    <div class="row g-3" id="supportTypeGroup">
                                        <div class="col-12">
                                            <button type="button" class="support-option-card active w-100" data-support-type="DONATE">
                                                <i class="bi bi-cash-coin"></i>
                                                <span>Donate</span>
                                                <small>Contribute to help maintain and improve the project</small>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-12 col-lg-6">
                                <div class="support-section-card h-100">
                                    <div class="section-title mb-3">2. Select Amount</div>
                                    <div class="row g-3" id="amountGroup">
                                        <div class="col-6 col-md-3">
                                            <button type="button" class="support-amount-card w-100" data-amount="100">₹100</button>
                                        </div>
                                        <div class="col-6 col-md-3">
                                            <button type="button" class="support-amount-card w-100" data-amount="200">₹200</button>
                                        </div>
                                        <div class="col-6 col-md-3">
                                            <button type="button" class="support-amount-card w-100" data-amount="500">₹500</button>
                                        </div>
                                        <div class="col-6 col-md-3">
                                            <button type="button" class="support-amount-card w-100" data-amount="1000">₹1000</button>
                                        </div>
                                    </div>

                                        <div class="support-summary mt-4">
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <span class="text-muted">Selected Type</span>
                                            <strong id="selectedSupportType">Donate</strong>
                                        </div>
                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                            <span class="text-muted">Selected Amount</span>
                                            <strong id="selectedAmountLabel">₹1000</strong>
                                        </div>
                                        <div class="d-grid mt-4">
                                            <button id="payNowBtn" class="btn btn-support btn-lg">
                                                <i class="bi bi-shield-check me-2"></i>Proceed to Razorpay Payment
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="support-footer text-center mt-4">
                            <div class="d-flex flex-column flex-sm-row justify-content-center align-items-center gap-3">
                                <small class="text-white-50">This is a public page and does not require login.</small>
                                <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-sm support-login-btn">
                                    <i class="bi bi-box-arrow-in-right me-1"></i>Back to Login
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        window.SUPPORT_APP = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
    <script src="https://checkout.razorpay.com/v1/checkout.js"></script>
    <script src="${pageContext.request.contextPath}/js/support.js"></script>
</body>
</html>