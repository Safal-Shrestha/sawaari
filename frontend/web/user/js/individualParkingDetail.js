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

        const parkingNameEl = document.getElementById("parkingName");
        const parkingAddressEl = document.getElementById("parkingAddress");

        if(parkingNameEl) parkingNameEl.textContent = parkingData.location;
        if(parkingAddressEl) parkingAddressEl.textContent = parkingData.address;

        console.log(slotData);

        const spotCount = slotData.length;
        const availableSpots = slotData.reduce((sum, p) => {
            if(p.isOccupied == false && p.isReserved == false) {
                return sum+1;
            }else {
                return sum;
            }
        }, 0);

        const reservedSpots = slotData.reduce((sum,p) => {
            if(p.isReserved == true) {
                return sum+1;
            }else {
                return 0
            }
        });

        const bookedSpots = slotData.reduce((sum,p) => {
            if(p.isBooked == true) {
                return sum+1;
            }else {
                return 0
            }
        });

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

        slotListContainer.innerHTML = "";

        slotData.forEach(p => {
            const card = document.createElement("div");

            if(p.isOccupied == true){
                card.className = "bg-red-100 border-2 border-red-500 rounded-lg flex items-center justify-center aspect-square text-red-700 font-bold";
                if(p.slotType == "TWO_WHEELER") {
                    card.innerHTML = "B"+p.slotNumber;
                } else {
                    card.innerHTML = "C"+p.slotNumber;
                }
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

                    selectedSlot = {
                        slotNumber: p.slotNumber,
                        card: card
                    };
                });
            }


            slotListContainer.appendChild(card);
        });


    }catch (err) {

    }

}

document.addEventListener("DOMContentLoaded", loadParkingDetails);