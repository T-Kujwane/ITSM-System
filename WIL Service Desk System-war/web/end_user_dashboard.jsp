
<%@page import="za.ac.tut.model.TicketUpdate"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.ZoneId"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="za.ac.tut.model.bean.TicketService"%>
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
        <%
            User usr = (User)session.getAttribute("user");
            
            if (usr == null){
                response.sendRedirect("login-page?resource=user");
            }else if (usr.getRoleId() == 1){
        %>
                <header>
                    <div class="navbar">
                        <h1>End User Dashboard</h1>
                        <nav>
                            <ul>
                                <li><a href="new-ticket">Create Ticket</a></li>
                                <li><a href="logout">Logout</a></li>
                            </ul>
                        </nav>
                    </div>
                </header>
                
                <section>
                    <h2>Logged in as: <%= usr.getFullName()%></h2>
                </section>

                <main class="container">
                    <h2>Your Tickets</h2>
            <%
                List<Ticket> tickets = null;
                Context ctx = new InitialContext();
                UserService userService = (UserService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/UserService");
                TicketService ticketService = (TicketService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/TicketServiceBean");
                
                if (userService != null) {
                    tickets = ticketService.getTicketsByUserId(usr.getUserId());
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
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                for (Ticket ticket : tickets) {
                                    String userTicketID = ticket.getUserTicketID(); 
                                    String ticketTitle = ticket.getTitle();
                                    String ticketStatus = ticket.getStatus().toUpperCase();
                                    LocalDateTime creationTime = ticket.getCreatedAt().toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDateTime();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy 'at' HH:mm");
                                    String date = creationTime.format(formatter);
                                    String description = ticket.getDescription();
                            %>
                                    <tr>
                                        <td><%= userTicketID%></td>
                                        <td><%= ticketTitle%></td>
                                        <td><%= ticketStatus%></td>

                                        <td><%= date%></td>
                                        <td><%= description%></td>
                                        <%
                                            System.out.println(ticket.getJSUpdates());
                                        %>
                                        <td>
                                            <button class="action-link" 
                                                    onclick="showTicketProgress('<%= userTicketID%>',
                                                                                 '<%= ticketTitle%>',
                                                                                 '<%= ticketStatus%>',
                                                                                 '<%= date%>',
                                                                                 '<%= description%>',
                                                                                 '<%= ticket.getJSUpdates()%>')">
                                                View Progress
                                            </button>

                                        </td>
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
                <!-- Modal for Ticket Management -->
                <div id="ticketModal" class="modal">
                    <div class="modal-content">
                        <span class="close" onclick="closeModal()">&times;</span>
                        <h2>Ticket Details</h2>
                        <form id="ticketForm">
                            
                            <label for="reference">Reference:</label>
                            <input id="reference" readonly="">
                            
                            <label for="title">Title:</label>
                            <input type="text" id="title" readonly>

                            <label for="description">Description:</label>
                            <textarea id="description" readonly></textarea>

                            <label for="status">Status:</label>
                            <input id="status" readonly="">
                            
                            <label for="date">Date Created:</label>
                            <input id="date" readonly="">
                            
                            <section id="updates_section">
                                
                            </section>
                            <br>
                            <button type="button" class="action-button" onclick="closeModal()" style="background-color: #dc3545;">Close</button>
                        </form>
                    </div>
                </div>
            <%
                // Notification Logic
                String msg = request.getParameter("msg");
                if (msg != null) {
                    boolean isError = msg.toLowerCase().contains("failed");
            %>
                    <script>
                        showNotification('<%= msg%>', <%= isError%>);
                    </script>
            <%
                }
            %>
            <script type="text/javascript" src="view_tickets.js"></script>
        <%
            } else {
        %>
                <h3 style="color: red;">You are forbidden from using this functionality. 
                    Contact the <a href="mailto:ThandekaBrad@gmail.com">system administrator</a> for any queries.
                </h3>
                <h4 class="nav-link"><a href="logout.jsp">Logout</a></h4>
        <%
            }
        %>
        <footer>
            <p>&copy; <%= LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>
    </body>
</html> 
