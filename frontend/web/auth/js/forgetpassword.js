// Forgot Password Form Handler
document.addEventListener('DOMContentLoaded', function() {
    // Configuration
    const API_BASE_URL = 'http://localhost:8080/api';

    const forgotForm = document.getElementById('forgotForm');
    const emailInput = document.getElementById('email');
    const submitBtn = document.getElementById('submitBtn');

    if (!forgotForm) {
        console.error('Form not found!');
        return;
    }

    // Clear any stored data when page loads
    sessionStorage.removeItem('resetEmail');
    sessionStorage.removeItem('otpVerified');

    forgotForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const email = emailInput.value.trim();
        // Add this at the top of your submit handler, after e.preventDefault()
console.log('Form submitted');
console.log('Email:', email);
console.log('Sending to:', `${API_BASE_URL}/ForgotPassword`);

        // Basic validation
        if (!email) {
            showMessage('Please enter your email address', 'error');
            return;
        }

        if (!isValidEmail(email)) {
            showMessage('Please enter a valid email address', 'error');
            return;
        }

        // Disable button and show loading state
        setButtonLoading(true);

        try {
            const response = await fetch(`${API_BASE_URL}/ForgotPassword`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email: email })
            });

            const message = await response.text();

            if (response.ok) {
                // Success - store email in sessionStorage and redirect to OTP page
                sessionStorage.setItem('resetEmail', email);
                showMessage(message || 'OTP sent to your email', 'success');

                // Redirect after 1.5 seconds
                setTimeout(() => {
                    window.location.href = 'OTP.html';
                }, 1500);
            } else {
                // Handle different error cases
                if (response.status === 404) {
                    showMessage('Email not found. Please check and try again.', 'error');
                } else if (response.status === 500) {
                    showMessage(message || 'Failed to send OTP. Please try again.', 'error');
                } else {
                    showMessage(message || 'Something went wrong. Please try again.', 'error');
                }
            }

        } catch (error) {
            console.error('Error:', error);
            showMessage('Network error. Please check your connection and try again.', 'error');
        } finally {
            setButtonLoading(false);
        }
    });

    // Email validation function
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Show message to user
    function showMessage(message, type) {
        // Remove existing message if any
        const existingMessage = document.querySelector('.message-box') || document.querySelector('.alert-message');
        if (existingMessage) {
            existingMessage.remove();
        }

        // Create message element
        const messageDiv = document.createElement('div');
        messageDiv.className = `message-box ${type}`;
        messageDiv.style.cssText = `
            padding: 12px 16px;
            margin-bottom: 16px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            ${type === 'success' 
                ? 'background-color: #d1fae5; color: #065f46; border: 1px solid #6ee7b7;' 
                : 'background-color: #fee2e2; color: #991b1b; border: 1px solid #fca5a5;'}
        `;
        messageDiv.textContent = message;

        // Insert before form
        if (forgotForm && forgotForm.parentElement) {
            forgotForm.parentElement.insertBefore(messageDiv, forgotForm);
        } else {
            document.body.insertBefore(messageDiv, document.body.firstChild);
        }

        // Auto-remove error messages after 5 seconds
        if (type === 'error') {
            setTimeout(() => {
                messageDiv.remove();
            }, 5000);
        }
    }

    // Set button loading state
    function setButtonLoading(isLoading) {
        if (!submitBtn) return;
        const buttonText = submitBtn.querySelector('span');

        if (isLoading) {
            submitBtn.disabled = true;
            submitBtn.style.opacity = '0.6';
            submitBtn.style.cursor = 'not-allowed';
            if (buttonText) buttonText.textContent = 'Sending...';
            else submitBtn.textContent = 'Sending...';
        } else {
            submitBtn.disabled = false;
            submitBtn.style.opacity = '1';
            submitBtn.style.cursor = 'pointer';
            if (buttonText) buttonText.textContent = 'Submit';
            else submitBtn.textContent = 'Submit';
        }
    }
});