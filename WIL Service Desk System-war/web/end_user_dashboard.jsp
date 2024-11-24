
<%@page import="java.time.LocalDate"%>
<%@page import="java.util.List"%>
<%@page import="javax.naming.Context"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="za.ac.tut.model.bean.UserService"%>
<%@page import="za.ac.tut.model.User"%>
<%@page import="za.ac.tut.model.Ticket"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>End User Dashboard</title>
    <link rel="stylesheet" href="styles.css">
    <script src="notifications.js"></script>
</head>
<body>
    <header>
        <h1>End User Dashboard</h1>
        <p><a href="logout.jsp">Logout</a></p>
        <p><a href="create_ticket.jsp">Create Ticket</a></p>
    </header>

    <main class="container">

        <h2>Your Tickets</h2>

        <%
            UserService userService = null;
            List<Ticket> tickets = null;

            try {
                Context ctx = new InitialContext();
                userService = (UserService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/UserService");
            } catch (Exception e) {
                out.println("<p>Error retrieving service: " + e.getMessage() + "</p>");
                e.printStackTrace(); // Consider logging instead of printing to the output
            }

            User loggedInUser = (User) session.getAttribute("user");

            if (loggedInUser != null && userService != null) {
                try {
                    tickets = userService.getTicketsByUserId(loggedInUser.getUserId());
                } catch (Exception e) {
                    out.println("<p>Error fetching tickets: " + e.getMessage() + "</p>");
                    e.printStackTrace(); // Consider logging instead of printing to the output
                }
            }
        %>

        <!-- Ticket Display Section -->
        <%
            if (tickets != null && !tickets.isEmpty()) {
        %>
        <table>
            <thead>
                <tr>
                    <th>Ticket ID</th>
                    <th>Title</th>
                    <th>Status</th>
                    <th>Created On</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                <%
                    for (Ticket ticket : tickets) {
                %>
                <tr>
                    <td><%= ticket.getTicketId() %></td>
                    <td><%= ticket.getTitle() %></td>
                    <td><%= ticket.getStatus() %></td>
                    <td><%= ticket.getCreatedAt() %></td>
                    <td><%= ticket.getDescription() %></td>
                </tr>
                <%
                    }
                %>
            </tbody>
        </table>
        <%
            } else {
        %>
        <p>You have no tickets.</p>
        <%
            }
        %>
    </main>

    <footer>
        <p>&copy; <%= LocalDate.now().getYear() %> IT Service Management System</p>
    </footer>

    <%
        // Notification Logic
        String msg = request.getParameter("msg");
        if (msg != null) {
            boolean isError = msg.toLowerCase().contains("failed");
    %>
        <script>
            showNotification(<%= msg %>, <%= isError %>);
        </script>
    <%
        }
    %>
</body>
</html>