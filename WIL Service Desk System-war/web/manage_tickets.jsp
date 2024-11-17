<%-- 
    Document   : manage_tickets
    Created on : 17 Nov 2024, 1:20:32 AM
    Author     : Thandeka Matampane
--%>
<%@ page import="za.ac.tut.model.Ticket"%>
<%@ page import="za.ac.tut.model.User"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<html>
    <head>
        <title>Manage Ticket</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <%
            // Fetch the ticket object from request attribute
            Ticket ticket = (Ticket) request.getAttribute("ticket");
            if (ticket == null) {
                out.println("<h3>Ticket not found.</h3>");
                return;
            }

            User usr = (User) request.getSession().getAttribute("user");
        %>

        <h2>Manage Ticket</h2>
        <table>
            <tr><th>Ticket ID</th><td><%= ticket.getTicketId() %></td></tr>
            <tr><th>Title</th><td><%= ticket.getTitle() %></td></tr>
            <tr><th>Description</th><td><%= ticket.getDescription() %></td></tr>
            <tr><th>Status</th><td><%= ticket.getStatus() %></td></tr>
            <tr><th>Priority</th><td><%= ticket.getPriority() %></td></tr>
            <tr><th>Assigned To</th><td><%= ticket.getAssignedTo() == 0 ? "Not Assigned" : ticket.getAssignedTo() %></td></tr>
        </table>

        <!-- Update Status Form -->
        <form action="TicketManagementServlet" method="POST">
            <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">
            <input type="hidden" name="action" value="updateStatus">
            <label for="status">New Status:</label>
            <select name="status" id="status">
                <option value="OPEN" <%= ticket.getStatus().equals("OPEN") ? "selected" : "" %>>Open</option>
                <option value="IN_PROGRESS" <%= ticket.getStatus().equals("IN_PROGRESS") ? "selected" : "" %>>In Progress</option>
                <option value="ESCALATED" <%= ticket.getStatus().equals("ESCALATED") ? "selected" : "" %>>Escalated</option>
                <option value="RESOLVED" <%= ticket.getStatus().equals("RESOLVED") ? "selected" : "" %>>Resolved</option>
                <option value="CLOSED" <%= ticket.getStatus().equals("CLOSED") ? "selected" : "" %>>Closed</option>
            </select>
            <button type="submit">Update Status</button>
        </form>

        <% if (usr.getRoleId() == 4) { %>
        <!-- Assign Ticket Form (Manager Role Only) -->
        <form action="TicketManagementServlet" method="POST">
            <input type="hidden" name="ticketId" value="<%= ticket.getTicketId() %>">
            <input type="hidden" name="action" value="assignTicket">
            <label for="assignedTo">Assign to Technician:</label>
            <select name="assignedTo" id="assignedTo">
                <option value="0">--Select Technician--</option>
                <% 
                    List<User> technicians = (List<User>) request.getAttribute("technicians");
                    for (User technician : technicians) {
                %>
                    <option value="<%= technician.getUserId() %>" <%= ticket.getAssignedTo() == technician.getUserId() ? "selected" : "" %>>
                        <%= technician.getFullName() %>
                    </option>
                <% } %>
            </select>
            <button type="submit">Assign</button>
        </form>
        <% } %>

        <a href="ticket_list.jsp">Back to Ticket List</a>
    </body>
</html>
