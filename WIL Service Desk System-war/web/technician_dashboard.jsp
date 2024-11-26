<%@page import="za.ac.tut.model.bean.UserService"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.ZoneId"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="za.ac.tut.model.bean.TicketService"%>
<%@page import="za.ac.tut.model.User"%>
<%@ page import="za.ac.tut.model.Ticket" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>ITSM - Dashboard</title>
        <link rel="stylesheet" href="styles.css"> <!-- Link to external CSS -->
    </head>
    <body>

        <%-- Ensure the user is logged in --%>
        <%
            User usr = (User) request.getSession().getAttribute("user");
            if (usr == null) {
                response.sendRedirect("login-page?resource=technician");
                return;
            }
        %>

        <header>
            <div class="navbar">
                <h1>Technician Dashboard</h1>
                <nav>
                    <ul>
                        <li><a href="home">Home</a></li>
                        <li><a href="ticket_history.jsp">Ticket History</a></li>
                        <li><a href="profile.jsp">Profile</a></li>
                        <li><a href="logout">Logout</a></li>
                    </ul>
                </nav>
            </div>
        </header>

        <section>
            <h2>Logged in as: <%= usr.getFullName()%></h2>
        </section>

        <main class="container">
            <h2>Your Assigned Tickets</h2>
            
            <!-- Table to display the tickets -->
            <table>
                <thead>
                    <tr>
                        <th>Ticket ID</th>
                        <th>Title</th>
                        <th>Status</th>
                        <th>Priority</th>
                        <th>Created</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        Context context = new InitialContext();
                        TicketService ticketService = (TicketService) context.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/TicketServiceBean");
                        UserService userService = (UserService) context.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/UserService");
                        
                        // Retrieve the list of tickets passed from the backend
                        List<Ticket> ticketList = ticketService.getTicketsAssignedTo(usr.getUserId());
                        if (ticketList != null && !ticketList.isEmpty()) {
                            for (Ticket ticket : ticketList) {
                    %>
                                <tr>
                                    <td><%= ticket.getTicketId()%></td>
                                    <td><%= ticket.getTitle()%></td>
                                    <td><%= ticket.getStatus().toUpperCase()%></td>
                                    <td><%= ticket.getPriority().getPriorityLevel()%></td>
                                    <%
                                        LocalDateTime timestamp = ticket.getCreatedAt().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDateTime();

                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm");
                                        String creatorName = userService.getUserByID(ticket.getCreatedBy()).getFullName();
                                    %>
                                    <td><%= timestamp.format(formatter)%></td>
                                    <td>
                                        <button class="action-link" onclick="openTicketModal(<%= ticket.getTicketId() %>, '<%= ticket.getTitle() %>',
                                                    '<%= ticket.getDescription() %>', '<%= ticket.getStatus().toUpperCase() %>', 
                                                    '<%= ticket.getPriority().getPriorityLevel().toUpperCase() %>', '<%= creatorName %>', 
                                                    '<%= ticket.getCreatedAt() %>', '<%= ticket.getUpdatedAt() %>');">Manage
                                        </button>
                                    </td>
                                </tr>
                    <%
                        }
                    } else {
                    %>
                                <tr>
                                    <td colspan="5">No tickets assigned.</td>
                                </tr>
                    <% }%>
                </tbody>
            </table>
        </main>

        <!-- Modal for ticket details -->
        <div id="ticketModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeModal()">&times;</span>
                <h2>Ticket Details</h2>
                <form id="ticketForm" action="updateTicket" method="POST">
                    <!-- Hidden field for ticketId -->
                    <input type="hidden" id="ticketId" name="ticketId">
                    <input type="text" hidden value="technician" name="caller">
                    <input type="text" value="update" name="action" hidden>
                    
                    <!-- Title (readonly) -->
                    <label for="title">Title:</label>
                    <input type="text" id="title" name="title" required readonly>

                    <!-- Description (readonly) -->
                    <label for="description">Description:</label>
                    <textarea id="description" name="description" required readonly></textarea>

                    <label for="status">Status:</label>
                    <select id="status" name="status" required >
                        <option id="open_status" value="OPEN">Open</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="ESCALATED">Escalated</option>
                        <option value="RESOLVED">Resolved</option>
                        <option value="CLOSED">Closed</option>
                    </select>
                    
                    <label id="update_comment_label" for="update_comment_input" style="display: none;">Update Comment:</label>
                    <input type="text" name="comment" id="update_comment_input" required="" style="display: none;">
                    
                    <!-- Priority (readonly) -->
                    <label for="priority">Priority:</label>
                    <input type="text" name="priority" required readonly id="priority">

                    <!-- Created By (readonly) -->
                    <label for="createdBy">Created By:</label>
                    <input type="text" id="createdBy" name="createdBy" readonly>

                    <!-- Created At (readonly) -->
                    <label for="createdAt">Created At:</label>
                    <input type="text" id="createdAt" name="createdAt" readonly>

                    <!-- Updated At (readonly) -->
                    <label for="updatedAt">Updated At:</label>
                    <input type="text" id="updatedAt" name="updatedAt" readonly>

                    <!-- Save Changes button (hidden since we are just displaying data) -->
                    <button type="submit" class="action-button" style="display:block;">Save Changes</button>
                    <button type="button" class="action-button" onclick="closeModal()" style="background-color: #dc3545;">Cancel Update</button>
                </form>
            </div>
        </div>

        <footer>
            <p>&copy; <%= LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>

        <script src="notifications.js"></script>
        <script>
            // Helper function to format Date objects to the desired format ("24 November 2024 at 22:35")
            function formatDate(date) {
                if (!date) return "";
                const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
                return new Date(date).toLocaleDateString('en-US', options).replace(",", " at");
            }
            
            function getUpdateComment(originalTicketStatus){
                const commentLabel = document.getElementById("update_comment_label");
                const commentInput = document.getElementById("update_comment_input");
                
                const newTicketValue = document.getElementById("status").value;
                
                if(newTicketValue.toLocaleString().toLocaleLowerCase() === originalTicketStatus.toLocaleString().toLocaleLowerCase()){
                    commentLabel.style.display = "none";
                    commentInput.style.display = "none";
                }else {
                    commentLabel.style.display = "block";
                    commentInput.style.display = "block";
                }
            }
            
            // Function to open the ticket modal and populate the fields
            function openTicketModal(ticketId, title, description, status, priority, createdBy, createdAt, updatedAt) {
                // Populate the fields with the ticket data
                document.getElementById("ticketId").value = ticketId;
                document.getElementById("title").value = title;
                document.getElementById("description").value = description;
                document.getElementById("status").value = status;  // This will display the status
                document.getElementById("status").onchange = function (){
                    getUpdateComment(status);
                };
                
                document.getElementById("priority").value = priority; // Assuming priority is a string
                document.getElementById("open_status").disabled = true;
                // Add details of who created the ticket
                document.getElementById("createdBy").value = createdBy;  

                // Format and display created and updated timestamps
                document.getElementById("createdAt").value = formatDate(createdAt);
                document.getElementById("updatedAt").value = formatDate(updatedAt);

                // Display the modal
                document.getElementById("ticketModal").style.display = "block";
            }

            // Close modal function
            function closeModal() {
                // Close the modal by hiding it
                document.getElementById("ticketModal").style.display = "none";
                document.getElementById("update_comment_label").style.display = "none";
                document.getElementById("update_comment_input").style.display = "none";
            }
            
            window.onclick = function(event) {
                let modal = document.getElementById('ticketModal');
                if (event.target === modal) {
                    //modal.style.display = "none";
                    closeModal();
                }
            };
        </script>
        <%
            String msg = request.getParameter("msg");
            if (msg != null){
                if (msg.toLowerCase().contains("failed")){
        %>
                    <script>
                        showNotification("<%=msg%>", true);
                    </script>
        <%
                }else {
        %>
                    <script>
                        showNotification("<%=msg%>", false);
                    </script>
        <%
                }
            }        
        %>
       
    </body>
</html>
