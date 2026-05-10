<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1.0"><title>404 - Not Found</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light d-flex align-items-center justify-content-center" style="min-height:100vh">
<div class="text-center"><i class="bi bi-exclamation-circle text-warning" style="font-size:5rem"></i>
<h1 class="mt-3">404</h1><p class="text-muted fs-5">Page not found</p>
<a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary mt-3"><i class="bi bi-house me-1"></i>Back to Dashboard</a></div>
</body></html>
