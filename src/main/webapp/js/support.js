document.addEventListener('DOMContentLoaded', function () {
    const contextPath = window.SUPPORT_APP ? window.SUPPORT_APP.contextPath : '';
    const supportTypeButtons = document.querySelectorAll('[data-support-type]');
    const amountButtons = document.querySelectorAll('[data-amount]');
    const selectedSupportType = document.getElementById('selectedSupportType');
    const selectedAmountLabel = document.getElementById('selectedAmountLabel');
    const payNowBtn = document.getElementById('payNowBtn');
    const successAlert = document.getElementById('supportSuccessAlert');
    const failureAlert = document.getElementById('supportFailureAlert');

    let currentSupportType = 'DONATE';
    let currentAmount = '100';

    function setActiveButton(buttons, activeButton, activeClass) {
        buttons.forEach(function (button) {
            button.classList.remove(activeClass);
        });
        activeButton.classList.add(activeClass);
    }

    function showAlert(element, message, type) {
        if (!element) {
            return;
        }
        element.className = 'alert alert-' + type;
        element.textContent = message;
        element.classList.remove('d-none');
    }

    function hideAlert(element) {
        if (element) {
            element.classList.add('d-none');
            element.textContent = '';
        }
    }

    supportTypeButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            currentSupportType = button.getAttribute('data-support-type');
            selectedSupportType.textContent = currentSupportType === 'GIFT' ? 'Gift' : 'Donate';
            setActiveButton(supportTypeButtons, button, 'active');
            hideAlert(successAlert);
            hideAlert(failureAlert);
        });
    });

    amountButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            currentAmount = button.getAttribute('data-amount');
            selectedAmountLabel.textContent = '₹' + currentAmount;
            setActiveButton(amountButtons, button, 'active');
            hideAlert(successAlert);
            hideAlert(failureAlert);
        });
    });

    // Ensure initial UI reflects defaults (DONATE and 100)
    selectedSupportType.textContent = 'Donate';
    selectedAmountLabel.textContent = '₹100';
    // Mark the button matching currentAmount active (single selection)
    amountButtons.forEach(function (button) {
        if (button.getAttribute('data-amount') === currentAmount) {
            button.classList.add('active');
        } else {
            button.classList.remove('active');
        }
    });
    // Ensure support type button active matches currentSupportType
    supportTypeButtons.forEach(function (button) {
        if (button.getAttribute('data-support-type') === currentSupportType) {
            button.classList.add('active');
        } else {
            button.classList.remove('active');
        }
    });

    async function createOrder() {
        const response = await fetch(contextPath + '/support/create-order', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            },
            body: new URLSearchParams({
                supportType: currentSupportType,
                amount: currentAmount
            })
        });
        return response.json();
    }

    async function verifyPayment(payload) {
        const response = await fetch(contextPath + '/support/verify', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            },
            body: new URLSearchParams(payload)
        });
        return response.json();
    }

    async function markFailed(payload) {
        const response = await fetch(contextPath + '/support/payment-failed', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
            },
            body: new URLSearchParams(payload)
        });
        return response.json();
    }

    payNowBtn.addEventListener('click', async function () {
        hideAlert(successAlert);
        hideAlert(failureAlert);
        payNowBtn.disabled = true;
        payNowBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Preparing payment...';

        try {
            const orderResponse = await createOrder();
            if (!orderResponse.success) {
                throw new Error(orderResponse.message || 'Unable to create payment order.');
            }

            if (typeof Razorpay === 'undefined') {
                throw new Error('Razorpay checkout script failed to load.');
            }

            const options = {
                key: orderResponse.keyId,
                amount: orderResponse.amountInPaise,
                currency: orderResponse.currency,
                name: 'Smart Billing',
                description: currentSupportType === 'GIFT' ? 'Support Developer Gift' : 'Support Developer Donation',
                order_id: orderResponse.orderId,
                prefill: {
                    name: 'Supporter'
                },
                theme: {
                    color: '#4f46e5'
                },
                handler: async function (response) {
                    try {
                        const verifyResponse = await verifyPayment({
                            razorpay_order_id: response.razorpay_order_id,
                            razorpay_payment_id: response.razorpay_payment_id,
                            razorpay_signature: response.razorpay_signature
                        });

                        if (verifyResponse.success) {
                            window.location.href = verifyResponse.redirectUrl;
                            return;
                        }

                        throw new Error(verifyResponse.message || 'Payment verification failed.');
                    } catch (error) {
                        showAlert(failureAlert, error.message, 'danger');
                        payNowBtn.disabled = false;
                        payNowBtn.innerHTML = '<i class="bi bi-shield-check me-2"></i>Proceed to Razorpay Payment';
                    }
                },
                modal: {
                    ondismiss: function () {
                        showAlert(failureAlert, 'Payment was closed before completion.', 'warning');
                        payNowBtn.disabled = false;
                        payNowBtn.innerHTML = '<i class="bi bi-shield-check me-2"></i>Proceed to Razorpay Payment';
                    }
                }
            };
            const rzp = new Razorpay(options);

            rzp.on('payment.failed', async function (response) {
                try {
                    const failureResponse = await markFailed({
                        razorpay_order_id: orderResponse.orderId,
                        razorpay_payment_id: response?.error?.metadata?.payment_id || '',
                        reason: response?.error?.description || response?.error?.reason || 'Payment failed'
                    });

                    if (failureResponse.success) {
                        window.location.href = failureResponse.redirectUrl;
                        return;
                    }
                } catch (error) {
                    showAlert(failureAlert, error.message, 'danger');
                }

                payNowBtn.disabled = false;
                payNowBtn.innerHTML = '<i class="bi bi-shield-check me-2"></i>Proceed to Razorpay Payment';
            });

            rzp.open();
        } catch (error) {
            showAlert(failureAlert, error.message, 'danger');
            payNowBtn.disabled = false;
            payNowBtn.innerHTML = '<i class="bi bi-shield-check me-2"></i>Proceed to Razorpay Payment';
        }
    });
});
