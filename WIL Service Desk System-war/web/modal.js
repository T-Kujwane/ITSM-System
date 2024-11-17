// modal.js
function openTicketModal(ticketId, title, description, status, priority) {
    let modal = document.getElementById('ticketModal');
    let ticketForm = document.getElementById('ticketForm');

    // Set the ticket details in the modal form
    document.getElementById('ticketId').value = ticketId;
    document.getElementById('title').value = title;
    document.getElementById('description').value = description;
    document.getElementById('status').value = status.toLocaleUpperCase();
    document.getElementById('priority').value = priority.toLocaleUpperCase();

    // Show the modal
    modal.style.display = "block";
}

function closeModal() {
    let modal = document.getElementById('ticketModal');
    modal.style.display = "none";
}

// Close the modal if clicked outside of the modal content
window.onclick = function(event) {
    let modal = document.getElementById('ticketModal');
    if (event.target == modal) {
        modal.style.display = "none";
    }
};
