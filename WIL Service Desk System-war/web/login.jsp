<%-- 
    Document   : dashboard
    Created on : 15 Sept 2024, 02:00:59
    Author     : Thandeka Matampane
--%>

<%@page import="java.time.LocalDate"%>
<%@page import="za.ac.tut.model.User"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>
    <head>
        <title>Login</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <header>
            <h2>Login</h2>
        </header>
        <%
            User usr = (User) request.getSession().getAttribute("user");
            if (usr != null){
                String redirectURL;
                switch(usr.getRoleId()){
                    case 1:     //End user
                        redirectURL = "user";
                        break;
                    case 2:     //Service desk agent
                        redirectURL = "agent";
                        break;
                    case 3:     //Technician
                        redirectURL = "technician";
                        break;
                    default:    //Manager
                        redirectURL = "manager";
                }
                response.sendRedirect(redirectURL);
                return;
            }
            
            String loginURL = "login";
            if (request.getParameter("resource") != null) {
                loginURL = loginURL.concat("?resource=".concat(request.getParameter("resource")));
            }
            
            String errorMsg = request.getParameter("error");
            
            boolean hasErrorMessage = errorMsg != null;
        %>
        <form action="<%= loginURL %>" method="post">
            <label>Username:</label><input type="text" value="<%= hasErrorMessage? !errorMsg.contains("username") ? request.getParameter("username") : "" : ""%>" name="username" required><br>
            <label>Password:</label><input type="password" value="<%= hasErrorMessage? !errorMsg.contains("password") ? request.getParameter("password") : "" : ""%>" name="password" required><br>
            <button type="submit">Login</button>
        </form>
        
        <%
            if (hasErrorMessage && ! errorMsg.equalsIgnoreCase("null")) { 
        %>
            <p style="color: red;"><%=errorMsg%>. Please try again.</p>
        <% } %>

        <!-- Links for Forgot Password and Register -->
        <footer>
            <p>
                <a href="forgot_password.jsp">Forgot Password?</a> | 
                <a href="create_account.jsp">Create Account</a>
            </p>
            <p>&copy; <%= LocalDate.now().getYear()%> IT Service Management System</p>
        </footer>

    </body>
</html>
