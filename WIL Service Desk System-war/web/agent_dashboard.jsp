<%-- 
    Document   : agent_dashboard
    Created on : 17 Sept 2024, 21:56:08
    Author     : Thato Keith Kujwane
--%>

<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Agent Dashboard</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <header>
            <h1>Agent Dashboard</h1>
        </header>

        <main class="container">
            <h2>Manage Tickets</h2>
            <p><a href="manage_tickets.jsp">View and Manage Tickets</a></p>
        </main>

        <footer>
            <p>&copy; <%= LocalDate.now().getYear()%> IT Service Management System</p>
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
