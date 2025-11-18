async function loadParkingInfo() {
    try {
        const res = await fetch("http://127.0.0.1:8080/api/parkingInfo");
        const data = await res.json();

        // Overview elements
        const availableLocationsEl = document.getElementById("availableLocations");
        const totalSpotsEl = document.getElementById("totalSpots");

        // Calculations
        const locationCount = data.length;
        const totalSpots = data.reduce((sum, p) => {
            return sum + (p.twoWheelerSpaceCount + p.fourWheelerSpaceCount);
        }, 0);

        // Update overview
        if (availableLocationsEl) availableLocationsEl.textContent = locationCount;
        if (totalSpotsEl) totalSpotsEl.textContent = totalSpots;

        // Populate locations list
        const listContainer = document.getElementById("locations-list");
        if (!listContainer) return;

        listContainer.innerHTML = "";

        data.forEach(p => {
            const card = document.createElement("div");
            card.className =
                "bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300 group";

            card.innerHTML = `
                <div class="relative">
                <div class="w-full h-48 bg-center bg-no-repeat bg-cover" style='background-image: url("${p.imageLink}");'></div>
                <div class="absolute top-2 right-2 bg-green-100 text-green-800 text-xs font-semibold px-2.5 py-0.5 rounded-full">Available</div>
                </div>
                <div class="p-4">
                <h4 class="text-lg font-semibold text-[var(--text-primary)] truncate">${p.location}</h4>
                <p class="text-sm text-[var(--text-secondary)] mt-1">1.2 miles away</p>
                <div class="flex justify-between items-center mt-3">
                <p class="text-lg font-bold text-[var(--primary-color)]">$15<span class="text-sm font-normal text-[var(--text-secondary)]">/day</span></p>
                <a class="bg-[var(--primary-color)] text-white px-4 py-2 text-sm font-semibold rounded-lg hover:bg-[var(--accent-color)]/80 focus:outline-none focus:ring-2 focus:ring-[var(--primary-color)] focus:ring-opacity-50 transition ease-in-out duration-150" href="availability.html">View Slots</a>
                </div>
                </div>
            `;

            listContainer.appendChild(card);
        });

    } catch (err) {
        console.error("Failed to load parking info:", err);
    }
}

// Ensure HTML is loaded before running
document.addEventListener("DOMContentLoaded", loadParkingInfo);
