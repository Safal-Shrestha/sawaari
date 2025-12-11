import refreshTokenService from "../../auth/js/refreshtoken.js";

const dateFormatter = new Intl.DateTimeFormat("en-US", {
    year: "numeric",
    month: "long",
    day: "numeric"
});

const timeFormatter = new Intl.DateTimeFormat("en-US", {
    hour: "numeric",
    minute: "2-digit",
    hour12: true
});

async function loadBookingDetails() {
    try {
        const bookingResponse = await refreshTokenService.get("/api/getBookingDetails");

        const bookingData = await bookingResponse.data;

        const reservedContainer = document.getElementById("upcoming-list");
        const activeContainer = document.getElementById("active-list");
        const historyContainer = document.getElementById("history-list");

        bookingData.forEach(p => {
            const bookingCard = document.createElement("div");
            bookingCard.className = "divide-y divide-gray-200 rounded-lg bg-white shadow-sm ring-1 ring-gray-900/5";
            if(p.status == "RESERVED") {
                const start = new Date(p.startTimestamp);
                const end = new Date(p.endTimestamp);

                const datePart = dateFormatter.format(start);
                const startTime = timeFormatter.format(start);
                const endTime = timeFormatter.format(end);

                bookingCard.innerHTML = `<div class="flex flex-col gap-4 p-6 sm:flex-row sm:items-center sm:justify-between">
                    <div class="flex items-center gap-4">
                    <div class="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-lg bg-[var(--secondary-color)] text-[var(--primary-color)]">
                    <svg class="h-6 w-6" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.125-.504 1.125-1.125V14.25m-17.25 4.5v-1.875a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 010 12.75v-1.5c0-.621.504-1.125 1.125-1.125H2.25l1.12-3.36a3.375 3.375 0 013.187-2.27h6.886a3.375 3.375 0 013.187 2.27l1.12 3.36H21.75c.621 0 1.125.504 1.125 1.125v1.5c0 .621-.504 1.125-1.125 1.125h-1.5a3.375 3.375 0 00-3.375 3.375v1.875" stroke-linecap="round" stroke-linejoin="round"></path>
                    </svg>
                    </div>
                    <div>
                    <p class="text-base font-semibold text-[var(--text-primary)]">${datePart}, ${startTime} - ${endTime} (${p.durationMinutes/60} hour)</p>
                    <p class="text-sm text-[var(--text-secondary)]">${p.location}</p>
                    </div>
                    </div>
                <div class="flex items-center gap-4">
                <span class="inline-flex items-center rounded-full bg-blue-100 px-2.5 py-0.5 text-xs font-medium text-blue-800">Confirmed</span>
                <button class="text-sm font-medium text-[var(--primary-color)] hover:text-[var(--accent-color)]">View Details</button>
                </div>
                </div>`;

                reservedContainer.appendChild(bookingCard);
            }

            if(p.status == "OCCUPIED") {
                const start = new Date(p.startTimestamp);
                const end = new Date(p.endTimestamp);

                const datePart = dateFormatter.format(start);
                const startTime = timeFormatter.format(start);
                const endTime = timeFormatter.format(end);

                bookingCard.innerHTML = `<div class="flex flex-col gap-4 p-6 sm:flex-row sm:items-center sm:justify-between">
                <div class="flex items-center gap-4">
                <div class="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-lg bg-[var(--secondary-color)] text-[var(--primary-color)]">
                    <svg class="h-6 w-6" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                    <path d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.125-.504 1.125-1.125V14.25m-17.25 4.5v-1.875a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 010 12.75v-1.5c0-.621.504-1.125 1.125-1.125H2.25l1.12-3.36a3.375 3.375 0 013.187-2.27h6.886a3.375 3.375 0 013.187 2.27l1.12 3.36H21.75c.621 0 1.125.504 1.125 1.125v1.5c0 .621-.504 1.125-1.125 1.125h-1.5a3.375 3.375 0 00-3.375 3.375v1.875" stroke-linecap="round" stroke-linejoin="round"></path>
                    </svg>
                </div>
                <div>
                    <p class="text-base font-semibold text-[var(--text-primary)]">${datePart}, ${startTime} - ${endTime} (${p.durationMinutes/60} hour)</p>
                    <p class="text-sm text-[var(--text-secondary)]">${p.location}</p>
                </div>
                </div>
                <div class="flex items-center gap-4">
                <span class="inline-flex items-center rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-800">Active</span>
                <button class="text-sm font-medium text-[var(--primary-color)] hover:text-[var(--accent-color)]">View Details</button>
                </div>
                </div>`;
                activeContainer.appendChild(bookingCard);
            }

            if(p.status == "COMPLETED" || p.status == "CANCELLED") {
                const start = new Date(p.startTimestamp);
                const end = new Date(p.endTimestamp);

                const datePart = dateFormatter.format(start);
                const startTime = timeFormatter.format(start);
                const endTime = timeFormatter.format(end);

                bookingCard.innerHTML = `<div class="flex flex-col gap-4 p-6 sm:flex-row sm:items-center sm:justify-between">
                <div class="flex items-center gap-4">
                <div class="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-lg bg-gray-100 text-gray-500">
                <svg class="h-6 w-6" fill="none" stroke="currentColor" stroke-width="1.5" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M8.25 18.75a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h6m-9 0H3.375a1.125 1.125 0 01-1.125-1.125V14.25m17.25 4.5a1.5 1.5 0 01-3 0m3 0a1.5 1.5 0 00-3 0m3 0h1.125c.621 0 1.125-.504 1.125-1.125V14.25m-17.25 4.5v-1.875a3.375 3.375 0 00-3.375-3.375h-1.5A1.125 1.125 0 010 12.75v-1.5c0-.621.504-1.125 1.125-1.125H2.25l1.12-3.36a3.375 3.375 0 013.187-2.27h6.886a3.375 3.375 0 013.187 2.27l1.12 3.36H21.75c.621 0 1.125.504 1.125 1.125v1.5c0 .621-.504 1.125-1.125 1.125h-1.5a3.375 3.375 0 00-3.375 3.375v1.875" stroke-linecap="round" stroke-linejoin="round"></path>
                </svg>
                </div>
                <div>
                <p class="text-base font-semibold text-[var(--text-primary)]">${datePart}, ${startTime} - ${endTime} (${p.durationMinutes/60} hour)</p>
                <p class="text-sm text-[var(--text-secondary)]">${p.location}</p>
                <p class="text-sm text-[var(--text-secondary)]">321 Maple Street, Anytown</p>
                </div>
                </div>
                <div class="flex items-center gap-4">
                <span class="inline-flex items-center rounded-full bg-gray-100 px-2.5 py-0.5 text-xs font-medium text-gray-800">Completed</span>
                <button class="text-sm font-medium text-[var(--primary-color)] hover:text-[var(--accent-color)]">View Details</button>
                </div>
                </div>`;

                historyContainer.appendChild(bookingCard);
            }
        });
    }catch(err) {console.log(err);}
}

document.addEventListener("DOMContentLoaded", loadBookingDetails());