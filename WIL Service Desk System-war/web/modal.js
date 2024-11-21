// modal.js
function openTicketModal(ticketId, title, description, status, priority, assignedTechnicianID) {
    let modal = document.getElementById('ticketModal');
    let ticketForm = document.getElementById('ticketForm');
    console.log('Ticket ID:', ticketId);
    console.log('Title:', title);
    console.log('Description:', description);
    console.log('Status:', status);
    console.log('Priority:', priority);
    console.log('Assigned Technician ID:', assignedTechnicianID);
    // Set the ticket details in the modal form
    document.getElementById('ticketId').value = ticketId;
    document.getElementById('title').value = title;
    document.getElementById('description').value = description;
    document.getElementById('status').value = status.toLocaleUpperCase();
    document.getElementById('priority').value = priority.toLocaleUpperCase();
    document.getElementById('assign_technician').value = assignedTechnicianID;

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
