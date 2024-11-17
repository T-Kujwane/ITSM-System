<%-- 
    Document   : logout
    Created on : 19 Oct 2024, 7:34:29 PM
    Author     : Thato Keith Kujwane
--%>

<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ITSM System - Logout</title>
        <link rel="stylesheet" href="styles.css">

        <style>
            /* Loading Spinner Styles */
            .loader {
                border: 8px solid #f3f3f3; /* Light grey */
                border-top: 8px solid #3498db; /* Blue */
                border-radius: 50%;
                width: 50px;
                height: 50px;
                animation: spin 1s linear infinite;
                margin: 20px auto;
            }

            @keyframes spin {
                0% {
                    transform: rotate(0deg);
                }
                100% {
                    transform: rotate(360deg);
                }
            }
        </style>

        <script>
            // Redirect after 3 seconds
            setTimeout(function () {
                window.location.href = "index.jsp";
            }, 3000);
        </script>
    </head>
    <body>
        <header>
            <h1>IT Service Management System</h1>
        </header>
        <main class="container">`
            <h2>Logging Out</h2>
            <p>You have been logged out successfully.</p>
            <p>Redirecting to the homepage...</p>
            <div class="loader"></div> <!-- Loading spinner -->
        </main>
        <footer>
            <p>&copy; <%=LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>
        <%
            //Thread.sleep(2000);
            // Invalidate the session to log out the user
            request.getSession().setAttribute("user", null);
            request.getSession().invalidate();
            // Redirect to the login page or index page after logging out

            // Set response headers to prevent caching
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
            response.setHeader("Pragma", "no-cache"); // HTTP 1.0
            response.setDateHeader("Expires", 0); // Proxies
        %>
    </body>
</html>
