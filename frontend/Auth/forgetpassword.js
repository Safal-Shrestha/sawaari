// Forgot Password Form Handler
document.addEventListener('DOMContentLoaded', function() {
    console.log('Script loaded successfully');
    
    const forgotForm = document.getElementById('forgotForm');
    const emailInput = document.getElementById('email');
    const submitBtn = document.getElementById('submitBtn');

    console.log('Form element:', forgotForm);
    console.log('Email input:', emailInput);
    console.log('Submit button:', submitBtn);

    // API Base URL - Update this to match your backend server
    const API_BASE_URL = 'http://localhost:8080/api';

    if (!forgotForm) {
        console.error('Form not found!');
        return;
    }

    forgotForm.addEventListener('submit', async function(e) {
        e.preventDefault();
        console.log('Form submitted!');

        const email = emailInput.value.trim();

        // Validate email
        if (!email) {
            showMessage('Please enter your email address', 'error');
            return;
        }

        if (!isValidEmail(email)) {
            showMessage('Please enter a valid email address', 'error');
            return;
        }

        // Disable button and show loading state
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="truncate">Sending OTP...</span>';

        // Create clean request body - ONLY email
        const requestBody = { email: email };
        
        console.log('=== REQUEST DEBUG ===');
        console.log('Request URL:', `${API_BASE_URL}/ForgotPassword`);
        console.log('Request body:', requestBody);
        console.log('Request body JSON:', JSON.stringify(requestBody));

        try {
            console.log('Sending request to:', `${API_BASE_URL}/ForgotPassword`);
            
            const response = await fetch(`${API_BASE_URL}/ForgotPassword`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestBody)
            });
            
            console.log('Response status:', response.status);
            console.log('Response ok:', response.ok);
            
            const data = await response.text();
            console.log('Response data:', data);

            if (response.ok) {
                // Success - OTP sent
                console.log('SUCCESS - Showing success message');
                showMessage(data, 'success');
                
                // Store email in sessionStorage for OTP verification page
                sessionStorage.setItem('resetEmail', email);
                
                // Redirect to OTP verification page after 2 seconds
                setTimeout(() => {
                    window.location.href = 'verify-otp.html';
                }, 2000);
            } else {
                // Error response from server
                console.log('ERROR - Showing error message:', data);
                showMessage(data, 'error');
            }

        } catch (error) {
            console.error('CATCH ERROR:', error);
            showMessage('Network error. Please check your connection and try again.', 'error');
        } finally {
            // Re-enable button
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span class="truncate">Submit</span>';
        }
    });

    // Email validation helper
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    // Show message to user
    function showMessage(message, type) {
        console.log('=== SHOW MESSAGE CALLED ===');
        console.log('Message:', message);
        console.log('Type:', type);
        
        // Remove any existing messages
        const existingMessage = document.querySelector('.alert-message');
        if (existingMessage) {
            console.log('Removing existing message');
            existingMessage.remove();
        }

        // Create message element
        const messageDiv = document.createElement('div');
        messageDiv.className = `alert-message p-4 rounded-lg mb-4 ${
            type === 'success' 
                ? 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-200 border border-green-300 dark:border-green-700' 
                : 'bg-red-100 dark:bg-red-900/30 text-red-800 dark:text-red-200 border border-red-300 dark:border-red-700'
        }`;
        
        messageDiv.innerHTML = `
            <div class="flex items-center">
                <span class="material-symbols-outlined mr-2">${type === 'success' ? 'check_circle' : 'error'}</span>
                <span>${message}</span>
            </div>
        `;

        // Insert message before the form
        const formContainer = document.querySelector('.rounded-xl.border');
        console.log('Form container found:', formContainer);
        
        if (formContainer && formContainer.parentNode) {
            formContainer.parentNode.insertBefore(messageDiv, formContainer);
            console.log('Message inserted successfully');
        } else {
            console.error('Could not find form container!');
            // Fallback - insert at top of body
            document.body.insertBefore(messageDiv, document.body.firstChild);
            console.log('Message inserted at top of body as fallback');
        }

        // Auto-remove error messages after 5 seconds
        if (type === 'error') {
            setTimeout(() => {
                messageDiv.remove();
                console.log('Error message auto-removed after 5 seconds');
            }, 5000);
        }
    }
});