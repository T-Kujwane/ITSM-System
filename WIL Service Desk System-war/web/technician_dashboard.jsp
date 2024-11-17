<%-- 
    Document   : technician_dashboard
    Created on : 17 Sept 2024, 22:00:23
    Author     : Thato Keith Kujwane
--%>
<%@ page import="za.ac.tut.model.Ticket" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Technician Dashboard</title>
        <link rel="stylesheet" href="styles.css"> <!-- Link to external CSS -->
    </head>
    <body>
        <header>
            <div class="navbar">
                <h1>Technician Dashboard</h1>
                <nav>
                    <ul>
                        <li><a href="technician_dashboard.jsp">Dashboard</a></li>
                        <li><a href="ticket_history.jsp">Ticket History</a></li>
                        <li><a href="profile.jsp">Profile</a></li>
                        <li><a href="logout.jsp">Logout</a></li>
                    </ul>
                </nav>
            </div>
        </header>

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
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        // Retrieve the list of tickets passed from the backend
                        List<Ticket> ticketList = (List<Ticket>) request.getAttribute("ticketList");
                        if (ticketList != null && !ticketList.isEmpty()) {
                            for (Ticket ticket : ticketList) {
                    %>
                                <tr>
                                    <td><%= ticket.getTicketId()%></td>
                                    <td><%= ticket.getTitle()%></td>
                                    <td><%= ticket.getStatus().toUpperCase()%></td>
                                    <td><%= ticket.getPriority().getPriorityLevel()%></td>
                                    <td>
                                        <button class="action-link" onclick="openTicketModal(<%= ticket.getTicketId()%>, '<%= ticket.getTitle()%>', '<%= ticket.getDescription()%>', '<%= ticket.getStatus().toUpperCase()%>', '<%= ticket.getPriority().getPriorityLevel()%>')">Manage</button>
                                    </td>
                                </tr>
                    <%
                            }
                        } else {
                    %>
                            <tr>
                                <td colspan="5">No tickets assigned.</td>
                            </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </main>

        <!-- Modal for ticket details -->
        <div id="ticketModal" class="modal">
            <div class="modal-content">
                <span class="close" onclick="closeModal()">&times;</span>
                <h2>Ticket Details</h2>
                <form id="ticketForm">
                    <input type="hidden" id="ticketId" name="ticketId">
                    <label for="title">Title:</label>
                    <input type="text" id="title" name="title" required>

                    <label for="description">Description:</label>
                    <textarea id="description" name="description" required></textarea>

                    <label for="status">Status:</label>
                    <select id="status" name="status" required>
                        <option value="OPEN">Open</option>
                        <option value="IN_PROGRESS">In Progress</option>
                        <option value="ESCALATED">Escalated</option>
                        <option value="RESOLVED">Resolved</option>
                        <option value="CLOSED">Closed</option>
                    </select>

                    <label for="priority">Priority:</label>
                    <select id="priority" name="priority" required>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                    </select>

                    <button type="submit" class="action-button">Save Changes</button>
                </form>
            </div>
        </div>

        <footer>
            <p>&copy; <%= LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>

        <script src="js/notifications.js"></script>
        <script src="js/modal.js"></script> <!-- External script for modal functionality -->
        <%
            String msg = request.getParameter("msg");
            if (msg != null) {
                if (msg.toLowerCase().contains("failed")) {
        %>
                    <script>
                                showNotification('<%= msg%>', true);
                    </script>
        <%
                } else {
        %>
                    <script>
                        showNotification('<%= msg%>', false);
                    </script>
        <%
                }
            }
        %>
    </body>
</html>
