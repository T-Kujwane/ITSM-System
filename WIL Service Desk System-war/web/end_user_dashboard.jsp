<%-- 
    Document   : end_user_dashboard
    Created on : 17 Sept 2024, 21:54:23
    Author     : Thato Keith Kujwane
--%>

<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>End User Dashboard</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <header>
            <h1>End User Dashboard</h1>
        </header>

        <main class="container">
            <h2>Create a New Ticket</h2>
            <p><a href="create_ticket.jsp">Create Ticket</a></p>

            <h2>Your Tickets</h2>
            <p><a href="view_tickets.jsp">View Your Tickets</a></p>
        </main>

        <footer>
            <p>&copy; <%=LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>
        <script src="notifications.js"></script>
        <%
            String msg = request.getParameter("msg");
            if (msg != null){
                if (msg.toLowerCase().contains("failed")){
                
        %>
            <script>
                showNotification(<%=msg%>, true);
            </script>
        <%
                }else {
        %>
            <script>
                showNotification(<%=msg%>, false);
            </script>
        <%
                }
            }        
        %>
    </body>
</html>
