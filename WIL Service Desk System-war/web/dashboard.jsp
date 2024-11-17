<%-- 
    Document   : dashboard
    Created on : 15 Sept 2024, 02:00:59
    Author     : Thato Keith Kujwane
--%>

<%@page import="za.ac.tut.model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head><title>Dashboard</title></head>
<body>
    <h2>Welcome, <%= ((User)session.getAttribute("user")).getFullName() %></h2>
    <a href="create_ticket.jsp">Create Ticket</a> | <a href="TicketServlet">View My Tickets</a>
</body>
</html>
