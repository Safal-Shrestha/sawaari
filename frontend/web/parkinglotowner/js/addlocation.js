import refreshTokenService from "../../auth/js/refreshtoken.js";

const accessToken = sessionStorage.getItem("accessToken");

document.getElementById("locationImage").addEventListener("change", function () {
    const file = this.files[0];
    const preview = document.getElementById("imagePreview");

    if (file) {
        const img = document.createElement("img");
        img.src = URL.createObjectURL(file);
        img.className = "image-preview";
        preview.innerHTML = "";
        preview.appendChild(img);
    } else {
        preview.innerHTML = "<p class='text-[#617589]'>No image selected</p>";
    }
});

document.getElementById("locationForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append("location", document.getElementById("locationName").value);
    formData.append("address", document.getElementById("fullAddress").value);
    formData.append("two_wheeler_space_count", document.getElementById("bikeSlots").value);
    formData.append("four_wheeler_space_count", document.getElementById("carSlots").value);
    formData.append("latitude", document.getElementById("latitude").value);
    formData.append("longitude", document.getElementById("longitude").value);
    formData.append("bike_rate", document.getElementById("bikeRate").value);
    formData.append("car_rate", document.getElementById("carRate").value);
    formData.append("is_active", true);
    formData.append("image", document.getElementById("locationImage").files[0]);

    try {
        const response = await refreshTokenService.post("/api/addNewParking", formData, {
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        const result = await response.json();

        if (result.status === "success") {
            alert(result.message);
            document.getElementById("locationForm").reset();
            document.getElementById("imagePreview").innerHTML = "<p>No image selected</p>";
        } else {
            alert("Error: " + result.message);
        }

    } catch (error) {
        alert("Something went wrong: " + error);
    }
});
