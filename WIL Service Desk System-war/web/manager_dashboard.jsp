<%@page import="za.ac.tut.model.User"%>
<%@page import="java.util.List"%>
<%@page import="za.ac.tut.model.Ticket"%>
<%@page import="za.ac.tut.model.bean.TicketService"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.time.LocalDate"%>
<html>
    <head>
        <title>Manager Dashboard</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <%
            User usr = (User) request.getSession().getAttribute("user");
            if (usr == null) {
                response.sendRedirect("login-page?resource=manager");
                return;
            }
        %>
        <header>
            <h1>Manager Dashboard</h1>
        </header>
        <section>
            <span>
                <h2>Logged in as: <%= usr.getFullName() %></h2>
            </span>
        </section>
        <main class="container">
            <%
                if(usr.getRoleId() == 4){
            %>
                <!-- Action Links for the Manager -->
                <section class="actions-section">
                    <h2>Actions</h2>
                    <ul class="actions-list">
                        <li><a href="create_ticket.jsp" class="action-link">Create Ticket</a></li>
                        <li><a href="create_user.jsp" class="action-link">Create User</a></li>
                        <li><a href="update_user.jsp" class="action-link">Update User</a></li>
                        <li><a href="view_reports.jsp" class="action-link">View Reports</a></li>
                        <li><a href="generate_reports.jsp" class="action-link">Generate Report</a></li>
                       <!--  <li><a href="manage_tickets.jsp" class="action-link">Manage Tickets</a></li> -->
                        <li><a href="logout" class="action-link">Logout</a></li>
                    </ul>
                </section>
                <%
                    Context ctx = new InitialContext();
                    TicketService ticketService = (TicketService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/TicketServiceBean");
                    Integer totalTickets = ticketService.getAllTickets().size();
                    Integer escalatedTickets = ticketService.getTicketsByStatus("Escalated").size();
                    Integer openTickets = ticketService.getTicketsByStatus("Open").size();
                %>
                <!-- Summary Section for Manager Overview -->
                <section class="summary-section">
                    <h2>System Overview</h2>
                    <table class="summary-table">
                        <tr>
                            <th>Total Tickets</th>
                            <th>Open Tickets</th>
                            <th>Escalated Tickets</th>
                            <!--<th>Technicians</th>-->
                        </tr>
                        <tr>
                            <td><%=totalTickets%></td>
                            <td><%=openTickets%></td>
                            <td><%=escalatedTickets%></td>
                            <!--<td>${totalTechnicians}</td>-->
                        </tr>
                    </table>
                </section>

                <!-- Optional Alerts Section for Important Notifications -->
                <!-- Optional Alerts Section for Important Notifications -->
                <section>
                    <h2>Important Notifications</h2>
                    <p><%= request.getParameter("msg") != null ? 
                            request.getParameter("msg") : "No critical issues at this time."%>
                    </p>
                </section>
            <%} else {%>
                <h3 style="color: red;">You are forbidden from using this functionality. 
                    Contact the <a href="mailto:ThandekaBrad@gmail.com">system administrator</a> for any queries.
                </h3>
                <h4 class="nav-link"><a href="logout.jsp">Logout</a></h4>
            <%}%>
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
                showNotification("<%=msg%>", true);
            </script>
        <%
                }else {
        %>
            <script>
                showNotification("<%=msg%>", false);
            </script>
        <%
                }
            }        
        %>
    </body>
</html>
