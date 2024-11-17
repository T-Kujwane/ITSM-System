<%-- 
    Document   : create_user
    Created on : 15 Sept 2024, 02:55:41
    Author     : Thato Keith Kujwane
--%>

<%@page import="za.ac.tut.model.User"%>
<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Create User</title>
        <link rel="stylesheet" href="styles.css"> <!-- Link the CSS file -->
    </head>
    <body>
        <%
            User usr = (User) request.getSession().getAttribute("user");
            if (usr == null) {
                response.sendRedirect("login.jsp?resource=create_user.jsp");
                return;
            }
        %>
        <header>
            <h1>Create New User</h1>
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
        <a href="<%=userDashboard%>">Dashboard</a>
        
        <main class="container">
            <%
                if(usr.getRoleId() == 4){
            %>
                <section class="form-section">
                    <h2>Create User Form</h2>
                    <form action="createUser" method="post">
                        <label>Username:</label>
                        <input type="text" name="username" required><br>

                        <label>Password:</label>
                        <input type="password" name="password" required><br>

                        <label>Full Name:</label>
                        <input type="text" name="fullName" required><br>

                        <label>Email:</label>
                        <input type="email" name="email" required><br>

                        <label>Role:</label>
                        <select name="roleId" required>
                            <option value="2">Technician</option>
                            <option value="3">Agent</option>
                            <option value="4">Manager</option>
                        </select><br>

                        <button type="submit">Create User</button>
                    </form>
                </section>
            <%} else {%>
                <h3 style="color: red;">You are forbidden from using this functionality. 
                    Contact the <a href="mailto:ThandekaBrad@gmail.com">system administrator</a> for any queries.
                </h3>
                <h4 class="nav-link"><a href="logout.jsp">Logout</a></h4>
            <%}%>
            
        </main>

        <footer>
            <p>&copy; <%=LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>
    </body>
</html>
