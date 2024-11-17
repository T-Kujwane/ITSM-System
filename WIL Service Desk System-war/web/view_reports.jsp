<%@page import="javax.naming.Context"%>
<%@page import="shaded.com.google.gson.Gson"%>
<%@page import="java.time.LocalDate"%>
<%@ page import="za.ac.tut.model.User"%>
<%@ page import="za.ac.tut.model.bean.TicketService"%>
<%@ page import="za.ac.tut.model.Ticket"%>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.naming.NamingException" %>

<html>
    <head>
        <title>View Reports</title>
        <link rel="stylesheet" href="styles.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script> <!-- For chart visualizations -->
    </head>
    <body>
        <%
            // Assuming the user is already logged in
            User usr = (User) request.getSession().getAttribute("user");
            if (usr == null) {
                response.sendRedirect("login.jsp?resource=view_reports.jsp");
                return;
            }
        %>
        <header>
            <h1>Service Desk System Reports</h1>
        </header>
        <%
            Integer loggedInUserRoleID = usr.getRoleId();
            String userDashboard;
            switch(loggedInUserRoleID){
                case 1:
                    userDashboard = "end_user_dashboard.jsp";
                    break;
                case 2:
                    userDashboard = "agent_dashboard.jsp";
                    break;
                case 3:
                    userDashboard = "technician_dashboard.jsp";
                    break;
                default:
                    userDashboard = "manager_dashboard.jsp";
            }
        %>
        <section>
            <span>
                <h2>Logged in user: <%= usr.getFullName()%></h2>
            </span>
        </section>
        <a href="<%=userDashboard%>">Dashboard</a>
        <%
            // Only allow Manager (role_id 4) to view reports
            if (usr.getRoleId() != 4) {
        %>
            <h3 style="color: red;">You are forbidden from using this functionality. 
                Contact the <a href="mailto:ThandekaBrad@gmail.com">system administrator</a> for any queries.
            </h3>
            <h4 class="nav-link"><a href="logout.jsp">Logout</a></h4>
        <%
        } else {
            // Lookup the TicketService EJB
            Context ctx = new InitialContext();
            TicketService ticketService = (TicketService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/TicketServiceBean!za.ac.tut.model.bean.TicketService");

            // Declare variables to hold ticket data
            int totalTickets = 0;
            Map<String, Integer> statusCount = new HashMap<String, Integer>();
            Map<String, Integer> priorityCount = new HashMap<String, Integer>();

            try {
                // Get total number of tickets
                totalTickets = ticketService.getTotalTickets();

                // Fetch all tickets for status count
                List<Ticket> openTickets = ticketService.getTicketsByStatus(Ticket.Status.OPEN.toString());
                List<Ticket> inProgressTickets = ticketService.getTicketsByStatus("in_progress");
                List<Ticket> resolvedTickets = ticketService.getTicketsByStatus("resolved");
                List<Ticket> closedTickets = ticketService.getTicketsByStatus("closed");

                // Count tickets by status
                statusCount.put("Open", openTickets.size());
                statusCount.put("In Progress", inProgressTickets.size());
                statusCount.put("Resolved", resolvedTickets.size());
                statusCount.put("Closed", closedTickets.size());

                // Fetch tickets for priority count
                List<Ticket> lowPriorityTickets = ticketService.getTicketsByPriority("Low");
                List<Ticket> mediumPriorityTickets = ticketService.getTicketsByPriority("Medium");
                List<Ticket> highPriorityTickets = ticketService.getTicketsByPriority("High");
                List<Ticket> criticalPriorityTickets = ticketService.getTicketsByPriority("Critical");

                // Count tickets by priority
                priorityCount.put("Low", lowPriorityTickets.size());
                priorityCount.put("Medium", mediumPriorityTickets.size());
                priorityCount.put("High", highPriorityTickets.size());
                priorityCount.put("Critical", criticalPriorityTickets.size());

            } catch (Exception e) {
                e.printStackTrace();
                // Handle the exception
                return;
            }
        %>

        <!-- Report Content -->
        <main class="container">
            <h2>Total Tickets: <%= totalTickets%></h2>

            <h3>Tickets by Status:</h3>
            <ul>
                <% for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {%>
                <li><%= entry.getKey()%>: <%= entry.getValue()%></li>
                    <% }%>
            </ul>

            <!-- Visualization of Tickets by Status -->
            <canvas id="statusChart" width="400" height="200"></canvas>
            <script>
                var ctx = document.getElementById('statusChart').getContext('2d');
                var statusChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: <%= new Gson().toJson(statusCount.keySet())%>, // Use keys for labels
                        datasets: [{
                                label: 'Tickets by Status',
                                data: <%= new Gson().toJson(statusCount.values())%>, // Use values for data
                                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                                borderColor: 'rgba(75, 192, 192, 1)',
                                borderWidth: 1
                            }]
                    }
                });
            </script>

            <h3>Tickets by Priority:</h3>
            <ul>
                <% for (Map.Entry<String, Integer> entry : priorityCount.entrySet()) {%>
                <li><%= entry.getKey()%>: <%= entry.getValue()%></li>
                    <% }%>
            </ul>

            <!-- Visualization of Tickets by Priority -->
            <canvas id="priorityChart" width="400" height="200"></canvas>
            <script>
                var ctx = document.getElementById('priorityChart').getContext('2d');
                var priorityChart = new Chart(ctx, {
                    type: 'pie',
                    data: {
                        labels: <%= new Gson().toJson(priorityCount.keySet())%>, // Priority Labels
                        datasets: [{
                                label: 'Tickets by Priority',
                                data: <%= new Gson().toJson(priorityCount.values())%>, // Data for tickets
                                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
                                borderColor: '#fff',
                                borderWidth: 1
                            }]
                    }
                });
            </script>
        </main>

        <footer>
            <p>&copy; <%= LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>

        <%
            }
        %>

    </body>
</html>
