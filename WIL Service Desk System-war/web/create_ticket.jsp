<%@page import="za.ac.tut.model.bean.UserService"%>
<%@page import="za.ac.tut.model.bean.UserServiceBean"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.Context"%>
<%@page import="java.util.List"%>
<%@page import="za.ac.tut.model.User"%>
<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Create Ticket</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <%
            User loggedInUser = (User)request.getSession().getAttribute("user");
            
            if (loggedInUser == null) {
                response.sendRedirect("login.jsp?resource=create_ticket.jsp");
                return;
            }else {
        %>
        <!--This section will display the user's details-->
        <!--<header>
            <span><h2>Create a New Ticket</h2></span>
            <span>
                <lable>User</lable>
                <input disabled="" value="<%= loggedInUser.getFullName()%>">
            </span>
        </header>-->
        <span><h2>Create a New Ticket</h2></span>
        
        <%
            Integer loggedInUserRoleID = loggedInUser.getRoleId();
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
        
        <a href="<%=userDashboard%>">Dashboard</a>
        <form action="createTicket" method="post">
            <label>Title:</label><input type="text" name="title" required><br>
            <label>Description:</label><textarea name="description" required></textarea><br>
            <%
                if (loggedInUser.getRoleId() == 2 || loggedInUser.getRoleId() == 4) {
            %>
                <label>Assign to Technician:</label>
                <select name="assignedTo">
                <%
                    Context ctx = new InitialContext();
                    UserService userService = (UserService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/UserService");
                    List<User> technicians = userService.getUserByType("technician");
                    if (technicians.isEmpty()) {
                %>
                    <option value="-1">No technicians available yet</option>
                <% } else { %>
                        <option value="0">Do not assign</option>
                    <% for (User u : technicians) { %>
                        <option value="<%= u.getUserId() %>"><%= u.getFullName() %></option>
                    <% } %>
                <% } %>
                </select><br>
            <% } %>
            <button type="submit">Create Ticket</button>
        </form>
        <%}%>
        <footer>
            <p>&copy; <%= LocalDate.now().getYear() %> IT Service Management System</p>
        </footer>
        <script src="validation.js"></script>
    </body>
</html>
