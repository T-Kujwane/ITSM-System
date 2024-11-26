function showTicketProgress(userTicketID, ticketTitle, ticketStatus, date, description, updatesStr) {
    // Fill out the ticket details
    document.getElementById("reference").value = userTicketID;
    document.getElementById("title").value = ticketTitle;
    document.getElementById("description").value = description;
    document.getElementById("status").value = ticketStatus;
    document.getElementById("date").value = date;

    // Get the section where updates will be displayed
    const updateSection = document.getElementById("updates_section");
    
    // Clear any previous updates
    updateSection.innerHTML = '';
    
    const updatesTable = document.createElement('table');
    
    const updatesTableHeader = document.createElement('thead');
    
    const updatesTableHeaderRow = document.createElement('tr');
    
    //status#who#comment#time
    ["Ticket Status", "Author", "Comment", "Date"].forEach(function(headerText){
        const headerCell = document.createElement('th');
        headerCell.textContent = headerText;
        
        updatesTableHeaderRow.appendChild(headerCell);
    });
    
    updatesTableHeader.appendChild(updatesTableHeaderRow);
    updatesTable.appendChild(updatesTableHeader);
    
    const updatesTableBody = document.createElement('tbody');
    
    const updates = updatesStr.split("||");
    
    updates.forEach(function(update){
        const updateDetails = update.split("#");
        const updatesTableBodyRow = document.createElement('tr');
        
        if (updateDetails.length > 1){
            updateDetails.forEach(function(detail){
                const detailCell = document.createElement('td');
                detailCell.textContent = detail;
                updatesTableBodyRow.appendChild(detailCell);
            });
        }
        
        updatesTableBody.appendChild(updatesTableBodyRow);
    });
    
    updatesTable.appendChild(updatesTableBody);
    updateSection.appendChild(updatesTable);
    // Display the modal with the ticket details
    document.getElementById("ticketModal").style.display = "block";
}

// Close the modal
function closeModal() {
    document.getElementById("ticketModal").style.display = "none";
}


window.onclick = function (event) {
    let modal = document.getElementById('ticketModal');
    if (event.target === modal) {
        //modal.style.display = "none";
        closeModal();
    }
};