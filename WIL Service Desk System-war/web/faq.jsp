<%-- 
    Document   : dashboard
    Created on : 15 Sept 2024, 02:00:59
    Author     : Thandeka Matampane
--%>

<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ITSM System - FAQ</title>
        <link rel="stylesheet" href="styles.css"> <!-- Assuming your CSS is saved as styles.css -->
        
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

            /* Hide the main content initially */
            .hidden {
                display: none;
            }

            /* Main content styles */
            .container {
                display: none; /* Initially hide the content */
                text-align: center;
                margin: 20px auto;
            }
        </style>

        <script>
            // Delay to simulate loading time (optional - you can adjust or remove this)
            setTimeout(function () {
                // Show the main content after loading
                document.querySelector('.loader').style.display = 'none';
                document.querySelector('.container').style.display = 'block';
            }, 2000); // Change the duration as needed (2000ms = 2 seconds)
        </script>
    </head>
    <body>
        <header class="navbar">
            <h1>ITSM System - FAQ</h1>
            <nav>
                <ul class="actions-list">
                    <li><a class="nav-link" href="home">Home</a></li>
                    <li><a class="nav-link" href="create_ticket.jsp">Create Ticket</a></li>
                    <li><a class="nav-link" href="login-page">Login</a></li>
                    <li><a class="nav-link" href="faq">FAQ</a></li>
                </ul>
            </nav>
        </header>
        
        <main class="container">
            <!--<section class="actions-section">
                <nav>
                    <ul class="actions-list">
                        <li><a href="create_ticket.jsp">Create Ticket</a></li>
                        <li><a href="login-page">Login</a></li>
                        <li><a href="faq">Help/FAQ</a></li>
                    </ul>
                </nav>
            </section>-->
            <h2>Frequently Asked Questions</h2>
            <table>
                <thead>
                    <tr>
                        <th>Question</th>
                        <th>Answer</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>What is IT Service Management (ITSM)?</td>
                        <td>ITSM is a set of practices designed to manage and deliver IT services to meet the needs of the business.</td>
                    </tr>
                    <tr>
                        <td>How can I submit a support ticket?</td>
                        <td>You can submit a support ticket through the 'Create Ticket' form directly through the ITSM portal.</td>
                    </tr>
                    <tr>
                        <td>What are the service hours for technical support?</td>
                        <td>Technical support is available from 8:00 AM to 4:00 PM, Monday to Friday, excluding public holidays.</td>
                    </tr>
                    <tr>
                        <td>How long does it take to resolve a support ticket?</td>
                        <td>The resolution time may vary based on the complexity of the issue, but we strive to resolve all tickets within 24 hours.</td>
                    </tr>
                    <tr>
                        <td>Can I track the status of my support ticket?</td>
                        <td>Yes, once you have submitted a ticket, you will receive a confirmation along with a ticket number which you can use to track its status via the ITSM portal.</td>
                    </tr>
                </tbody>
            </table>
        </main>

        <div class="loader"></div> <!-- Loading spinner -->

        <footer>
            <p>&copy; <%=LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>

        <%
            // Simulate a loading time (if necessary)
            // Example: Thread.sleep(2000); // Uncomment if you want to delay before loading the FAQ content
        %>
    </body>
</html>
