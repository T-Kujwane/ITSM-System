<%@page import="za.ac.tut.model.bean.UserService"%>
<%@page import="za.ac.tut.model.User"%>
<%@page import="java.util.List"%>
<%@page import="za.ac.tut.model.Ticket"%>
<%@page import="za.ac.tut.model.bean.TicketService"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.time.LocalDate"%>
<html>
    <head>
        <title>Service Desk Agent Dashboard</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <%
            
            User usr = (User) request.getSession().getAttribute("user");
            if (usr == null) {
                response.sendRedirect("login-page?resource=agent");
                return;
            }
        %>
        <header>
            <h1>Service Desk Agent Dashboard</h1>
        </header>
        <section>
            <span>
                <h2>Logged in as: <%= usr.getFullName() %></h2>
            </span>
        </section>
        <main class="container">
            <%
                if (usr.getRoleId() == 2) {
            %>
                    <!-- Ticket Management Actions -->
                    <section class="actions-section">
                        <h2>Ticket Actions</h2>
                        <ul class="actions-list">
                            <li><a href="manage_tickets.jsp" class="action-link">Manage Tickets</a></li>
                            <li><a href="assign_ticket.jsp" class="action-link">Assign Ticket</a></li>
                            <li><a href="logout" class="action-link">Logout</a></li>
                        </ul>
                    </section>
                <!-- Ticket Overview Section -->
                <%
                    Context ctx = new InitialContext();
                    TicketService ticketService = (TicketService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/TicketServiceBean");
                    List<Ticket> allTickets = ticketService.getAllTickets(); // You can filter by status or other parameters as needed
                    List<Ticket> openTickets = ticketService.getTicketsByStatus("Open");
                    List<Ticket> escalatedTickets = ticketService.getTicketsByStatus("Escalated");
                    List<Ticket> inProgressTickets = ticketService.getTicketsByStatus("In_Progress");
                    List<Ticket> closedTickets = ticketService.getTicketsByStatus("Closed");
                    List<Ticket> resolvedTickets = ticketService.getTicketsByStatus("Resolved");
                %>

                    <section class="ticket-summary-section">
                        <h2>Ticket Overview</h2>
                        <table class="summary-table">
                            <tr>
                                <th>Total Tickets</th>
                                <th>Open Tickets</th>
                                <th>In Progress</th>
                                <th>Resolved</th>
                                <th>Escalated Tickets</th>
                                <th>Closed</th>
                            </tr>
                            <tr>
                                <td><%= allTickets.size() %></td>
                                <td><%= openTickets.size() %></td>
                                <td><%= inProgressTickets.size() %></td>
                                <td><%= resolvedTickets.size()%></td>
                                <td><%= escalatedTickets.size() %></td>
                                <td><%= closedTickets.size()%></td>
                            </tr>
                        </table>
                    </section>

            <!-- Ticket Listing Section -->
            <section>
                <h2>All Open and In-Progress Tickets</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Ticket ID</th>
                            <th>Title</th>
                            <th>Status</th>
                            <th>Priority</th>
                            <th>Assigned Technician</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%  
                            if (allTickets != null && !allTickets.isEmpty()) {
                                for (Ticket ticket : allTickets) {
                        %>
                                    <tr>
                                        <td><%= ticket.getTicketId() %></td>
                                        <td><%= ticket.getTitle() %></td>
                                        <td><%= ticket.getStatus().toUpperCase() %></td>
                                        <td><%= ticket.getPriority().getPriorityLevel().toUpperCase().replace("_", "")%></td>
                                        <td><%= ticket.getAssignedTo().getFullName()%></td>
                                        <td>
                                            <button class="action-link" onclick="openTicketModal(<%= ticket.getTicketId() %>, '<%= ticket.getTitle() %>', '<%= ticket.getDescription() %>', '<%= ticket.getStatus().toUpperCase() %>', '<%= ticket.getPriority().getPriorityLevel().toUpperCase()%>', <%= ticket.getAssignedTo().getUserId()%>);">Manage</button>
                                        </td>
                                    </tr>
                        <%
                                }
                            } else {
                        %>
                                    <tr>
                                        <td colspan="5">No tickets available at the moment.</td>
                                    </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </section>

            <% } else { %>
                <h3 style="color: red;">You are forbidden from using this functionality. Contact the <a href="mailto:admin@example.com">system administrator</a> for any queries.</h3>
                <h4><a href="logout.jsp">Logout</a></h4>
            <% } %>
        </main>

        <footer>
            <p>&copy; <%= LocalDate.now().getYear() %> IT Service Management System</p>
        </footer>

        <!-- Modal for Ticket Management -->
        <div id="ticketModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeModal()">&times;</span>
                <h2>Ticket Details</h2>
                <form id="ticketForm" action="updateTicket" method="POST">
                    <input type="text" hidden="" name="caller" value="agent">
                    <input hidden="" type="text" id="ticketId" name="ticketId">
                    <input type="text" hidden name="action" value="assign">
                    
                    <label for="title">Title:</label>
                    <input type="text" id="title" name="title" required readonly="">

                    <label for="description">Description:</label>
                    <textarea id="description" name="description" required readonly=""></textarea>

                    <label for="status">Status:</label>
                    <input type="text" id="status" name="status" required readonly>
                    
                    <label for="priority">Priority:</label>
                    <select id="priority" name="priority" required>
                        <option value="CRITICAL">Critical</option>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>
                    
                    <label for="assign_technician">Assign to Technician:</label>
                    <select name="assignedTo" id="assign_technician" required>
                    <%
                        Context ctx = new InitialContext();
                        UserService userService = (UserService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/UserService");
                        List<User> technicians = userService.getUserByType("technician");
                        if (technicians.isEmpty()) {
                    %>
                        <option value="-1" selected=>No technicians available yet</option>
                    <% } else { %>
                            <option value="1">Do not assign</option>
                        <% for (User u : technicians) { %>
                            <option value="<%= u.getUserId() %>"><%= u.getFullName() %></option>
                        <% } %>
                    <% } %>
                    </select><br>

                    <button type="submit" class="action-button">Save Changes</button>
                    <button type="button" class="action-button" onclick="closeModal()" style="background-color: #dc3545;">Cancel Update</button>
                </form>
            </div>
        </div>

        <script src="notifications.js"></script>
        <script src="modal.js"></script>
        <script>
            // Function to open the modal with ticket details
            function openTicketModal(ticketId, title, description, status, priority) {
                document.getElementById("ticketId").value = ticketId;
                document.getElementById("title").value = title;
                document.getElementById("description").value = description;
                document.getElementById("status").value = status;
                document.getElementById("priority").value = priority;
                document.getElementById("ticketModal").style.display = "block";
            }

            // Function to close the modal
            function closeModal() {
                document.getElementById("ticketModal").style.display = "none";
            }
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
