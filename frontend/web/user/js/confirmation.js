function confirmationDetails() {
    const bookingDetails = JSON.parse(localStorage.getItem("bookingDetails"));

    if (!bookingDetails) {
        alert("No booking details found.");
        return;
    }

    document.getElementById("location").textContent = bookingDetails.location;
    document.getElementById("slot-number").textContent = bookingDetails.slotNumber;
    document.getElementById("time").textContent = bookingDetails.time;
    document.getElementById("duration").textContent = bookingDetails.duration/60+"hrs";
    document.getElementById("total-price").textContent = bookingDetails.totalPrice;
}

document.addEventListener("DOMContentLoaded", confirmationDetails());