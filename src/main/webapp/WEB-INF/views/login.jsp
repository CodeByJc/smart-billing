<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Smart Billing System - Admin Login">
    <title>Smart Billing - Login</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body class="login-body">
    <div class="login-container">
        <!-- Decorative background elements -->
        <div class="login-bg-shape login-bg-shape-1"></div>
        <div class="login-bg-shape login-bg-shape-2"></div>
        <div class="login-bg-shape login-bg-shape-3"></div>

        <div class="login-card">
            <div class="login-card-header">
                <div class="login-logo">
                    <i class="bi bi-receipt-cutoff"></i>
                </div>
                <h1 class="login-title">Smart Billing</h1>
                <p class="login-subtitle">Billing & Inventory Management System</p>
            </div>

            <!-- Flash messages -->
            <c:if test="${not empty message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert" id="flashMessage">
                    <i class="bi bi-check-circle me-2"></i>${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Error message -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert" id="errorMessage">
                    <i class="bi bi-exclamation-triangle me-2"></i>${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/auth/login" method="post" id="loginForm"
                  onsubmit="return validateLoginForm()">
                <div class="mb-4">
                    <label for="username" class="form-label fw-semibold">
                        <i class="bi bi-person me-1"></i>Username
                    </label>
                    <input type="text" class="form-control form-control-lg" id="username" name="username"
                           value="${username}" placeholder="Enter your username" required autofocus>
                </div>

                <div class="mb-4">
                    <label for="password" class="form-label fw-semibold">
                        <i class="bi bi-lock me-1"></i>Password
                    </label>
                    <div class="input-group">
                        <input type="password" class="form-control form-control-lg" id="password" name="password"
                               placeholder="Enter your password" required>
                        <button class="btn btn-outline-secondary" type="button" id="togglePassword"
                                onclick="togglePasswordVisibility()">
                            <i class="bi bi-eye" id="togglePasswordIcon"></i>
                        </button>
                    </div>
                </div>

                <button type="submit" class="btn btn-primary btn-lg w-100 login-btn" id="loginBtn">
                    <i class="bi bi-box-arrow-in-right me-2"></i>Sign In
                </button>
            </form>

            <div class="login-footer">
                <div class="d-flex flex-column flex-sm-row align-items-center justify-content-between gap-2">
                    <small class="text-muted">Default: admin / admin123</small>
                    <a href="${pageContext.request.contextPath}/support" class="btn btn-sm support-developer-btn">
                        <i class="bi bi-heart-fill me-1"></i>Support Developer
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Client-side login form validation
        function validateLoginForm() {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();

            if (username === '') {
                showValidationError('username', 'Username is required');
                return false;
            }
            if (password === '') {
                showValidationError('password', 'Password is required');
                return false;
            }
            return true;
        }

        function showValidationError(fieldId, message) {
            const field = document.getElementById(fieldId);
            field.classList.add('is-invalid');
            // Remove existing feedback
            const existing = field.parentElement.querySelector('.invalid-feedback');
            if (existing) existing.remove();
            // Add feedback
            const feedback = document.createElement('div');
            feedback.className = 'invalid-feedback';
            feedback.textContent = message;
            field.parentElement.appendChild(feedback);
            field.focus();
        }

        // Toggle password visibility
        function togglePasswordVisibility() {
            const passwordField = document.getElementById('password');
            const icon = document.getElementById('togglePasswordIcon');
            if (passwordField.type === 'password') {
                passwordField.type = 'text';
                icon.className = 'bi bi-eye-slash';
            } else {
                passwordField.type = 'password';
                icon.className = 'bi bi-eye';
            }
        }

        // Clear validation errors on input
        document.querySelectorAll('input').forEach(input => {
            input.addEventListener('input', function() {
                this.classList.remove('is-invalid');
            });
        });
    </script>
</body>
</html>
