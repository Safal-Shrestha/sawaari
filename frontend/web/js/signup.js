function validatePasswords() {
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    if (password !== confirmPassword) {
        alert("Passwords do not match!");
        return false;
    }
    return true;
}
async function registerUser() {
    const data = {
        fullname: document.getElementById("fullname").value,
        username: document.getElementById("username").value,
        dob: document.getElementById("dob").value,
        gender: document.getElementById("gender").value,
        contact: document.getElementById("contact").value,
        country: document.getElementById("country").value,
        role: document.getElementById("role").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value
    };


    let response = await fetch("http://localhost:8080/api/userInfo", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

}
async function submitSignupForm() {
    if (!validatePasswords()) return;

    await registerUser();
}