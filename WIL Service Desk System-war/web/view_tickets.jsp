<%-- 
    Document   : dashboard
    Created on : 15 Sept 2024, 02:00:59
    Author     : Thandeka Matampane
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Tickets</title>
    <link rel="stylesheet" href="styles.css"> <!-- Include your stylesheets here -->
</head>
<body>
    <header>
        <h1>My Tickets</h1>
    </header>

    <main class="container">
        <h2>Your Tickets</h2>

        <!-- Show notification if any -->
        <c:if test="${not empty notification}">
            <div class="notification ${notification.type}">
                ${notification.message}
            </div>
        </c:if>

        <!-- User Info -->
        <p>Welcome, ${sessionScope.user.fullName}</p>

        <!-- Tickets Table -->
        <table>
            <thead>
                <tr>
                    <th>Ticket ID</th>
                    <th>Title</th>
                    <th>Status</th>
                    <th>Created On</th>
                    <th>Comment</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ticket" items="${tickets}">
                    <tr>
                        <td>${ticket.ticket_id}</td>
                        <td>${ticket.title}</td>
                        <td>${ticket.status}</td>
                        <td>${fn:substring(ticket.created_at, 0, 10)}</td>
                        <td>
                            <c:choose>
                                <c:when test="${ticket.status != 'resolved' && ticket.status != 'closed'}">
                                    <form action="addComment" method="post">
                                        <input type="hidden" name="ticket_id" value="${ticket.ticket_id}">
                                        <textarea name="comment" placeholder="Add a comment..." required></textarea>
                                        <button type="submit">Add Comment</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <span>No comment needed</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${ticket.status != 'resolved' && ticket.status != 'closed'}">
                                    <form action="addComment" method="post">
                                        <input type="hidden" name="ticket_id" value="${ticket.ticket_id}">
                                        <textarea name="comment" placeholder="Add a comment..." required></textarea>
                                        <button type="submit">Add Comment</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <span>No action available</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </main>

    <footer>
        <p>&copy; 2024 Service Desk System. All rights reserved.</p>
    </footer>
</body>
</html>
