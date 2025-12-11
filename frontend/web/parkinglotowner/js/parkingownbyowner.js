import refreshTokenService from "../../auth/js/refreshtoken.js";

async function fetchOwnerParking() {
    try {
        // Hard-coded owner_id for now
        const ownerId = 7;

        const response = await refreshTokenService.post(`/api/ownerParking?owner_id=${ownerId}`);
        const result = await response.json();

        const container = document.getElementById("ownerParkingContainer");
        container.innerHTML = "";

        if (result.status === "success" && result.parkingList.length > 0) {
            result.parkingList.forEach(parking => {
                const card = document.createElement("div");
                card.className = "bg-white dark:bg-background-dark/50 rounded-xl p-4 shadow-md mb-4";

                card.innerHTML = `
                    <h3 class="text-lg font-bold text-[#111418] dark:text-white">${parking.location}</h3>
                    <p class="text-sm text-[#617589] dark:text-gray-400">${parking.address}</p>
                    <p class="text-sm text-[#111418] dark:text-white">Car Slots: ${parking.fourWheelerSpaceCount}</p>
                    <p class="text-sm text-[#111418] dark:text-white">Bike Slots: ${parking.twoWheelerSpaceCount}</p>
                    <img src="${parking.imageLink}" alt="${parking.location}" class="mt-2 w-full max-h-48 object-cover rounded-lg"/>
                `;

                container.appendChild(card);
            });
        } else {
            container.innerHTML = `<p class="text-sm text-red-500">${result.message}</p>`;
        }

    } catch (error) {
        console.error("Error fetching parking:", error);
    }
}

// Call on page load
window.addEventListener("DOMContentLoaded", fetchOwnerParking);
