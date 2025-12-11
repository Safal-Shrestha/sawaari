import refreshTokenService from "../../auth/js/refreshtoken.js";

let selectedSlot = null;

const params = new URLSearchParams(window.location.search);
const value = params.get("id");
const parkingId = parseInt(value, 10);

async function loadParkingDetails() {
    try{
        const parkingByIdResponse = await refreshTokenService.get("/api/parkingById/"+parkingId);
        const parkingData = await parkingByIdResponse.data;

        const slotByParkingIdResponse = await refreshTokenService.get("/api/slotByParkingId/"+parkingId);
        const slotData = slotByParkingIdResponse.data;

        const vehicleOwned = await refreshTokenService.get("/api/vehicleById");
        const vehicleData = vehicleOwned.data;

        // console.log(vehicleData);

        const parkingNameEl = document.getElementById("parkingName");
        const parkingAddressEl = document.getElementById("parkingAddress");

        if(parkingNameEl) parkingNameEl.textContent = parkingData.location;
        if(parkingAddressEl) parkingAddressEl.textContent = parkingData.address;

        // console.log(slotData);

        const selectedSlotConfirmation = document.getElementById("spot-selection");
        const availableVehicleList = document.getElementById("vehicle-id");
        const selectedSlotIdInput = document.getElementById("spot-id");

        vehicleData.forEach(p => {
            availableVehicleList.innerHTML = `<option>${p.vid}</option>`;
        });

        availableVehicleList.insertAdjacentHTML(
            "beforeend",
            `<option>Add new vehicle...</option>`
        );

        const spotCount = slotData.length;
        const availableSpots = slotData.filter(p => !p.isReserved && !p.isOccupied).length;
        const reservedSpots = slotData.filter(p => p.isReserved).length;
        const bookedSpots = slotData.filter(p => p.isOccupied).length;

        const totalSpotsEl = document.getElementById("totalSlots");
        const availableSpotsEl = document.getElementById("availableSlots");
        const bookedSpotsEl = document.getElementById("bookedSlots");
        const reservedSpotsEl = document.getElementById("reservedSlots");

        const availabilityBarEl = document.getElementById("availabilityBar");
        const availablilityBarCard = document.createElement("div");

        availablilityBarCard.className = "bg-green-500 h-2.5 rounded-full";
        availablilityBarCard.style.width = (availableSpots/spotCount*100)+"%";

        availabilityBarEl.appendChild(availablilityBarCard);

        if (totalSpotsEl) totalSpotsEl.textContent = spotCount;
        if (availableSpotsEl) availableSpotsEl.textContent = availableSpots;
        if (bookedSpotsEl) bookedSpotsEl.textContent = bookedSpots;
        if (reservedSpotsEl) reservedSpotsEl.textContent = reservedSpots;

        const slotListContainer = document.getElementById("slot-list");
        if (!slotListContainer) return;

        const occupiedSlotsDetailContainer = document.getElementById("occupiedSlotsDetailList");
        if (!occupiedSlotsDetailContainer) return;

        slotListContainer.innerHTML = "";

        slotData.forEach(p => {
            const card = document.createElement("div");

            if(p.isOccupied == true){
                card.className = "bg-red-100 border-2 border-red-500 rounded-lg flex items-center justify-center aspect-square text-red-700 font-bold";
                const occupiedCard = document.createElement("div");
                occupiedCard.className = "block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100";
                if(p.slotType == "TWO_WHEELER") {
                    card.innerHTML = "B"+p.slotNumber;
                    occupiedCard.innerHTML = `<span class="font-semibold">B${p.slotNumber}</span>`;
                } else {
                    card.innerHTML = "C"+p.slotNumber;
                    occupiedCard.innerHTML = `<span class="font-semibold">C${p.slotNumber}</span>`;
                }

                occupiedSlotsDetailContainer.appendChild(occupiedCard);
            }
            else if(p.isReserved == true) {
                card.className = "bg-yellow-100 border-2 border-yellow-400 rounded-lg flex items-center justify-center aspect-square text-yellow-700 font-bold cursor-pointer hover:bg-yellow-200 transition";
                if(p.slotType == "TWO_WHEELER") {
                    card.innerHTML = "B"+p.slotNumber;
                } else {
                    card.innerHTML = "C"+p.slotNumber;
                }
            }
            else {
                card.className = "bg-green-100 border-2 border-green-500 rounded-lg flex items-center justify-center aspect-square text-green-700 font-bold cursor-pointer hover:bg-green-200 transition";
                if(p.slotType == "TWO_WHEELER") {
                    card.innerHTML = "B"+p.slotNumber;
                } else {
                    card.innerHTML = "C"+p.slotNumber;
                }
                card.addEventListener("click", () => {
                    if (selectedSlot && selectedSlot.card !== card) {
                        selectedSlot.card.className =
                            "bg-green-100 border-2 border-green-500 rounded-lg flex items-center justify-center aspect-square text-green-700 font-bold cursor-pointer hover:bg-green-200 transition";
                    }

                    card.className = "bg-blue-100 border-2 border-blue-500 rounded-lg flex items-center justify-center aspect-square text-blue-700 font-bold cursor-pointer hover:bg-blue-200 transition";

                    selectedSlotIdInput.value = p.slotId;
                    selectedSlotConfirmation.value = card.innerHTML;

                    selectedSlot = {
                        slotNumber: p.slotNumber,
                        slotId: p.slotId,
                        card: card
                    };
                });
            }


            slotListContainer.appendChild(card);
        });


    }catch (err) {

    }

}

async function submitReservation() {
    try{
        const slotId = document.getElementById("spot-id").value;
        const vehicleId = document.getElementById("vehicle-id").value;
        const startTime = document.getElementById("start-time").value;
        const durationText = document.getElementById("duration").value;

        let durationMinutes = 60;
        if (durationText.includes("30")) durationMinutes = 30;
        if (durationText.includes("1 hour")) durationMinutes = 60;
        if (durationText.includes("2 hours")) durationMinutes = 120;
        if (durationText.includes("3 hours")) durationMinutes = 180;

        if (!slotId) {
            alert("Please select a parking slot.");
            return;
        }
        if (vehicleId === "Add new vehicle...") {
            alert("Please select a valid vehicle.");
            return;
        }

        const payload = {
            slotId: parseInt(slotId),
            vehicleId: vehicleId,
            startTime: startTime,
            durationMinutes: durationMinutes
        };

        console.log("Payload going to backend:", payload);

        // const res = await refreshTokenService.post("/api/bookSlot", payload);

        // window.location.href = 'confirmation.html';


        console.log("Backend response:", res.data);
        alert("Reservation confirmed!");
    }catch(err) {}
}

document.addEventListener("DOMContentLoaded", () => {
    loadParkingDetails();

    const confirmBtn = document.getElementById("confirmReservationBtn");
    confirmBtn.addEventListener("click", submitReservation);
});