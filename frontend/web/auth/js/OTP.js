// OTP Verification Form Handler
document.addEventListener('DOMContentLoaded', function() {
    console.log('OTP Verification script loaded');
    
    // Get all OTP input fields
    const otpInputs = document.querySelectorAll('input[type="text"]');
    const verifyButton = document.querySelector('button');
    const resendLink = document.querySelector('a[href="#"]');
    const emailDisplay = document.querySelector('.font-medium');
    
    // API Base URL - Update this to match your backend server
    const API_BASE_URL = 'http://localhost:8080/api';
    
    // Get email from sessionStorage (set by forgot password page)
    const email = sessionStorage.getItem('resetEmail');
    
    console.log('Retrieved email from session:', email);
    
    // Display email in the page
    if (email && emailDisplay) {
        emailDisplay.textContent = email;
    } else if (!email) {
        // If no email found, redirect back to forgot password page
        showMessage('Session expired. Please request OTP again.', 'error');
        setTimeout(() => {
            window.location.href = 'forgetpassword.html';
        }, 2000);
        return;
    }
    
    // Focus first input on load
    if (otpInputs.length > 0) {
        otpInputs[0].focus();
    }
    
    // Add paste event listener to handle pasted OTP
    otpInputs[0].addEventListener('paste', function(e) {
        e.preventDefault();
        const pastedData = e.clipboardData.getData('text').trim();
        
        // Check if pasted data is 6 digits
        if (/^\d{6}$/.test(pastedData)) {
            // Distribute digits across inputs
            for (let i = 0; i < 6 && i < otpInputs.length; i++) {
                otpInputs[i].value = pastedData[i];
            }
            otpInputs[5].focus(); // Focus last input
        }
    });
    
    // Verify button click handler
    verifyButton.addEventListener('click', async function(e) {
        e.preventDefault();
        console.log('Verify button clicked');
        
        // Collect OTP from all inputs
        let otp = '';
        otpInputs.forEach(input => {
            otp += input.value;
        });
        
        console.log('Collected OTP:', otp);
        
        // Validate OTP
        if (otp.length !== 6) {
            showMessage('Please enter the complete 6-digit OTP', 'error');
            return;
        }
        
        if (!/^\d{6}$/.test(otp)) {
            showMessage('OTP must contain only digits', 'error');
            return;
        }
        
        // Disable button and show loading state
        verifyButton.disabled = true;
        verifyButton.innerHTML = '<span class="truncate">Verifying...</span>';
        
        // Disable all OTP inputs
        otpInputs.forEach(input => input.disabled = true);
        
        try {
            const requestBody = {
                email: email,
                otp: otp
            };
            
            console.log('Sending OTP verification request:', requestBody);
            
            const response = await fetch(`${API_BASE_URL}/VerifyOtp`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestBody)
            });
            
            console.log('Response status:', response.status);
            
            const data = await response.text();
            console.log('Response data:', data);
            
            if (response.ok) {
                // Success - OTP verified
                showMessage('OTP verified successfully! Redirecting...', 'success');
                
                // Keep email in sessionStorage for reset password page
                // It's already there, so no need to set again
                
                // Redirect to reset password page after 2 seconds
                setTimeout(() => {
                    window.location.href = 'resetpassword.html';
                }, 2000);
            } else {
                // Error - Invalid OTP
                showMessage(data, 'error');
                
                // Clear OTP inputs
                clearOtpInputs();
                
                // Re-enable inputs and button
                otpInputs.forEach(input => input.disabled = false);
                verifyButton.disabled = false;
                verifyButton.innerHTML = '<span class="truncate">Verify Code</span>';
                
                // Focus first input
                otpInputs[0].focus();
            }
            
        } catch (error) {
            console.error('Error during OTP verification:', error);
            showMessage('Network error. Please check your connection and try again.', 'error');
            
            // Re-enable inputs and button
            otpInputs.forEach(input => input.disabled = false);
            verifyButton.disabled = false;
            verifyButton.innerHTML = '<span class="truncate">Verify Code</span>';
        }
    });
    
    // Resend OTP handler
    resendLink.addEventListener('click', async function(e) {
        e.preventDefault();
        console.log('Resend OTP clicked');
        
        if (!email) {
            showMessage('Session expired. Please start over.', 'error');
            return;
        }
        
        // Disable resend link temporarily
        resendLink.style.pointerEvents = 'none';
        resendLink.style.opacity = '0.5';
        const originalText = resendLink.textContent;
        resendLink.textContent = 'Sending...';
        
        try {
            const requestBody = { email: email };
            
            console.log('Resending OTP for email:', email);
            
            const response = await fetch(`${API_BASE_URL}/ForgotPassword`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestBody)
            });
            
            const data = await response.text();
            console.log('Resend response:', data);
            
            if (response.ok) {
                showMessage('New OTP sent to your email!', 'success');
                clearOtpInputs();
                otpInputs[0].focus();
            } else {
                showMessage(data, 'error');
            }
            
        } catch (error) {
            console.error('Error resending OTP:', error);
            showMessage('Failed to resend OTP. Please try again.', 'error');
        } finally {
            // Re-enable resend link after 30 seconds (to prevent spam)
            setTimeout(() => {
                resendLink.style.pointerEvents = 'auto';
                resendLink.style.opacity = '1';
                resendLink.textContent = originalText;
            }, 30000);
        }
    });
    
    // Helper function to clear all OTP inputs
    function clearOtpInputs() {
        otpInputs.forEach(input => {
            input.value = '';
        });
    }
    
    // Helper function to show messages
    function showMessage(message, type) {
        console.log('=== SHOW MESSAGE ===');
        console.log('Message:', message);
        console.log('Type:', type);
        
        // Remove any existing messages
        const existingMessage = document.querySelector('.alert-message');
        if (existingMessage) {
            existingMessage.remove();
        }
        
        // Create message element
        const messageDiv = document.createElement('div');
        messageDiv.className = `alert-message p-4 rounded-lg mb-6 ${
            type === 'success' 
                ? 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-200 border border-green-300 dark:border-green-700' 
                : 'bg-red-100 dark:bg-red-900/30 text-red-800 dark:text-red-200 border border-red-300 dark:border-red-700'
        }`;
        
        messageDiv.innerHTML = `
            <div class="flex items-center justify-center">
                <span class="material-symbols-outlined mr-2">${type === 'success' ? 'check_circle' : 'error'}</span>
                <span>${message}</span>
            </div>
        `;
        
        // Insert message before the card
        const card = document.querySelector('.max-w-md.rounded-xl');
        if (card && card.parentNode) {
            card.parentNode.insertBefore(messageDiv, card);
        }
        
        // Auto-remove messages after 5 seconds
        setTimeout(() => {
            messageDiv.remove();
        }, 5000);
    }
    
    // Allow Enter key to submit from any input
    otpInputs.forEach(input => {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                verifyButton.click();
            }
        });
    });
    
    console.log('OTP Verification script initialization complete');
});