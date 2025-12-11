let devId = localStorage.getItem("deviceId");

if (!devId) {
  deviceId = crypto.randomUUID();
  localStorage.setItem("deviceId", deviceId);
}

async function loginUser(event) {
    event.preventDefault();
    
    // Get message box element
    const messageBox = document.getElementById('messageBox');
    
    // Hide any previous messages
    if (messageBox) {
        messageBox.className = 'hidden mt-4 p-4 rounded-lg';
    }
    
    // Get form values
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    
    // Validate email
    if (!email) {
        showMessage("Email is required", "error");
        document.getElementById('email').focus();
        return;
    }
    
    // Validate password
    if (!password) {
        showMessage("Password is required", "error");
        document.getElementById('password').focus();
        return;
    }
    
    // Prepare login data
    const loginData = {
        username: email,
        password: password,
        deviceId: devId
    };
    
    // Disable button and show loading state
    const submitBtn = document.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = 'Logging in...';
    
    try {
        let response = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify(loginData)
        });
        
        // Log response details
        console.log("Response status:", response.status);
        console.log("Response ok:", response.ok);
        
        if (response.ok) {
            showMessage("Login successful! Redirecting to dashboard...", "success");
            
            const data = await response.json();
            const accessToken = data.accessToken;

            sessionStorage.setItem("accessToken", accessToken);

            if(data.role == "ADMIN")  {
                setTimeout(() => {
                    window.location.href = "/frontend/web/admin/landing.html";
                }, 10);
            }

            if(data.role == "GENERAL_USER")  {
                setTimeout(() => {
                    window.location.href = "/frontend/web/user/dashboard.html";
                }, 10);
            }

            if(data.role == "PARKING_OWNER")  {
                setTimeout(() => {
                    window.location.href = "/frontend/web/parkinglotowner/dashboard.html";
                }, 10);
            }
        } else {
            // Show the exact error from backend
            showMessage(message, "error");
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
        
    } catch (error) {
        console.error("Error:", error);
        showMessage("Failed to connect to server! Please check if the backend is running.", "error");
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalText;
    }
}

// Function to display messages on the page
function showMessage(message, type) {
    const messageBox = document.getElementById('messageBox');
    
    if (!messageBox) {
        console.error("Message box not found!");
        return;
    }
    
    if (type === 'success') {
        messageBox.className = 'mt-4 mb-4 p-4 rounded-lg bg-green-50 border border-green-200 text-green-800';
        messageBox.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                </svg>
                <span class="font-medium">${message}</span>
            </div>
        `;
    } else {
        messageBox.className = 'mt-4 mb-4 p-4 rounded-lg bg-red-50 border border-red-200 text-red-800';
        messageBox.innerHTML = `
            <div class="flex items-center">
                <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                </svg>
                <span class="font-medium">${message}</span>
            </div>
        `;
    }
    
    // Scroll to the message
    messageBox.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// Function to toggle password visibility
function togglePassword(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);
    
    if (!input || !icon) {
        console.error("Input or icon not found!");
        return;
    }
    
    if (input.type === 'password') {
        input.type = 'text';
        // Change to "eye-off" icon
        icon.innerHTML = `
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>
        `;
    } else {
        input.type = 'password';
        // Change back to "eye" icon
        icon.innerHTML = `
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
        `;
    }
}

// Initialize form handler when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    
    const loginForm = document.getElementById('loginForm');
    
    if (loginForm) {
        loginForm.addEventListener('submit', loginUser);
    } else {
        console.error("Login form not found!");
    }
});