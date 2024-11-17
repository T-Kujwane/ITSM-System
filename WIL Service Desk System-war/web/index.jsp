<%-- 
    Document   : index
    Created on : 17 Sept 2024, 21:58:35
    Author     : Thandeka Matampane
--%>

<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Welcome to Our ITSM</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <header>
            <h1>Welcome to Our IT Service Management System</h1>
        </header>
        <main class="container">
            <section class="actions-section">
                <nav>
                    <ul class="actions-list">
                        <li><a href="create_ticket.jsp">Create Ticket</a></li>
                        <li><a href="login.jsp">Login</a></li>
                        <li><a href="faq.jsp">Help/FAQ</a></li>
                    </ul>
                </nav>
            </section>
            
            <section>
                <h2>Need Help?</h2>
                <p>Our system helps you create and manage tickets for all your IT needs.</p>
            </section>
        </main>
        <footer>
            <p>&copy; <%=LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>
    </body>
</html>