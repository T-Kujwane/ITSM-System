<%@page import="za.ac.tut.model.User"%>
<%@ page import="java.util.*" %>
<html>
    <head>
        <title>Update User</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <%
            User usr = (User) request.getSession().getAttribute("user");
            if (usr == null) {
                response.sendRedirect("login.jsp?resource=update_user.jsp");
                return;
            }
        %>
        <header>
            <span><h1>Update User</h1></span>
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
                <!-- Notification Box -->
                <div id="notification" class="notification"></div>

                <!-- Search Section -->
                <section>
                    <h2>Search User</h2>
                    <form action="userSearch" method="GET">
                        <input type="text" name="caller" value="update_user.jsp" hidden="">
                        <input type="text" name="query" placeholder="Enter username or email" required>
                        <button type="submit">Search</button>
                    </form>

                    <%
                        String msg = request.getParameter("msg");
                    %>
                    <%
                        if (msg != null){
                            if(msg.toLowerCase().contains("not found")){
                    %>
                                <h3 style="color: #dc3545;"><%=msg%></h3>
                                <% }else if ( ! msg.toLowerCase().contains("successful")) {%>
                                <%User user = (User) session.getAttribute("searchUser");%>
                                <section class="form-section">
                                    <form action="updateUser" method="POST">
                                        <input type="text" name="caller" value="update_user.jsp" hidden="">
                                        <input type="text" name="userID" hidden="" value="<%=user.getUserId()%>" required>

                                        <label>Username:</label>
                                        <input type="text" name="username" value="<%=user.getUsername()%>" required><br>

                                        <label>Full Name:</label>
                                        <input type="text" name="fullName" value="<%=user.getFullName()%>" required><br>

                                        <label>Email:</label>
                                        <input type="email" name="email" value="<%= user.getEmail()%>" readonly required><br>

                                        <label>Role:</label>
                                        <select name="roleId" required>
                                            <%if (user.getRoleId() == 2){%>
                                                <option value="2" selected="">Service Desk Agent</option>
                                                <option value="3">Technician</option>
                                                <option value="4">Manager</option>
                                            <%} else if (user.getRoleId() == 3){%>
                                                <option value="2">Service Desk Agent</option>
                                                <option value="3" selected="">Technician</option>
                                                <option value="4">Manager</option>
                                            <%} else if(user.getRoleId() == 4){%>
                                                <option value="2">Service Desk Agent</option>
                                                <option value="3">Technician</option>
                                                <option value="4" selected="">Manager</option>
                                            <%}%>

                                        </select><br>

                                        <input type="submit" value="Update User" name="action"> <br/>
                                        <input type="submit" value="Delete User" name="action" style="background-color: #dc3545;">
                                    </form>
                                </section>
                    <%      }
                        }
                    %>
                </section>
                
                <script src="notifications.js"></script>
                <%
                    if (msg != null){
                        if (msg.toLowerCase().contains("failed")){
                %>
                            <script>
                                showNotification("<%=msg%>", true);
                            </script>
                <%
                        } else if (msg.toLowerCase().contains("successful")){
                %>
                            <script>
                                showNotification("<%=msg%>", false);
                            </script>
                <%
                        }
                    }        
                %>
            <%} else {%>
                <h3 style="color: red;">You are forbidden from using this functionality. 
                    Contact the <a href="mailto:ThandekaBrad@gmail.com">system administrator</a> for any queries.
                </h3>
                <h4 class="nav-link"><a href="logout.jsp">Logout</a></h4>
            <%}%>
        </main>

        <footer>
            <p>&copy; 2024 IT Service Management System</p>
        </footer>
        
    </body>
</html>
