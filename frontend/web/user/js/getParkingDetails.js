const accessToken = sessionStorage.getItem("accessToken");

function getCurrentLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Geolocation not supported"));
      return;
    }

    navigator.geolocation.getCurrentPosition(
      pos => {
        resolve({
          lat: pos.coords.latitude,
          lon: pos.coords.longitude
        });
      },
      err => reject(err),
      { enableHighAccuracy: true }
    );
  });
}

function distanceInMeters(lat1, lon1, lat2, lon2) {
  const R = 6371000; 
  const toRad = deg => deg * Math.PI / 180;

  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);

  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) *
    Math.cos(toRad(lat2)) *
    Math.sin(dLon / 2) ** 2;

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}


async function loadParkingInfo() {
  try {
        const current = await getCurrentLocation();

        const res = await fetch("http://127.0.0.1:8080/api/parkingInfo", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        const data = await res.json();

        const slotResponse = await fetch("http://127.0.0.1:8080/api/slotInfo", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        const slotData = await slotResponse.json();

        const monthlySpendingResponse = await fetch("http://127.0.0.1:8080/api/monthlySpending", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });

        const monthlySpendingData = await monthlySpendingResponse.json();

        // Overview elements
        const availableLocationsEl = document.getElementById("availableLocations");
        const totalSpotsEl = document.getElementById("totalSpots");
        const activeBookingSpotsEl = document.getElementById("activeBooking");
        const monthlySpendingEl = document.getElementById("monthlySpending");

        // Calculations
        const locationCount = data.length;
        const totalSpots = data.reduce((sum, p) => {
            return sum + (p.twoWheelerSpaceCount + p.fourWheelerSpaceCount);
        }, 0);
        const activeBookings = slotData.bookedSlots;
        const monthlySpending = monthlySpendingData.monthlySpending;

        // Update overview
        if (availableLocationsEl) availableLocationsEl.textContent = locationCount;
        if (totalSpotsEl) totalSpotsEl.textContent = totalSpots;
        if (activeBookingSpotsEl) activeBookingSpotsEl.textContent = activeBookings;
        if (monthlySpendingEl) monthlySpendingEl.textContent = monthlySpending;

        // Populate locations list
        const listContainer = document.getElementById("locations-list");
        if (!listContainer) return;

        listContainer.innerHTML = "";

        data.forEach(p => {
            const card = document.createElement("div");
            const placeLat = p.latitude;
            const placeLon = p.longitude;
            const distance = distanceInMeters(
              current.lat,
              current.lon,
              placeLat,
              placeLon
            );

            // console.log(current.lat);
            card.className =
                "bg-white rounded-xl shadow-md overflow-hidden hover:shadow-xl transition-shadow duration-300 group";

            card.innerHTML = `
                <div class="relative">
                <div class="w-full h-48 bg-center bg-no-repeat bg-cover" style='background-image: url("${p.imageLink}");'></div>
                <div class="absolute top-2 right-2 bg-green-100 text-green-800 text-xs font-semibold px-2.5 py-0.5 rounded-full">Available</div>
                </div>
                <div class="p-4">
                <h4 class="text-lg font-semibold text-[var(--text-primary)] truncate">${p.location}</h4>
                <p class="text-sm text-[var(--text-secondary)] mt-1">${(distance/1000).toFixed(2)} kms away</p>
                <div class="flex justify-between items-center mt-3">
                <div>
                    <p class="text-lg font-bold text-[var(--primary-color)]">🚴Rs.${p.twoWheelerRatePerHour}<span class="text-sm font-normal text-[var(--text-secondary)]">/day</span></p>
                    <p class="text-lg font-bold text-[var(--primary-color)]">🚘Rs.${p.fourWheelerRatePerHour}<span class="text-sm font-normal text-[var(--text-secondary)]">/day</span></p>
                </div>
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

document.addEventListener("DOMContentLoaded", loadParkingInfo);