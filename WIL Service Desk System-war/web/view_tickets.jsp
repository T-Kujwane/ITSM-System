<%-- 
    Document   : view_tickets
    Created on : 15 Sept 2024, 01:56:42
    Author     : Thato Keith Kujwane
--%>

<%@page import="java.util.List"%>
<%@page import="za.ac.tut.model.Ticket"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>
    <head>
        <title>View Tickets</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <h2>Your Tickets</h2>
        <table border="1">
            <tr><th>Title</th><th>Status</th><th>Action</th></tr>
            <c:forEach var="ticket" items="${tickets}">
                <tr>
                    <td>${ticket.title}</td>
                    <td>${ticket.status}</td>
                    <td>
                        <form action="updateTicket" method="post">
                            <input type="hidden" name="ticketId" value="${ticket.ticketId}">
                            <select name="status">
                                <option value="in_progress">In Progress</option>
                                <option value="escalated">Escalate</option>
                                <option value="resolved">Resolved</option>
                            </select>
                            <input type="text" name="comment" placeholder="Add comment">
                            <button type="submit">Update</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </body>
</html>
