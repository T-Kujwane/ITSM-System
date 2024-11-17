package za.ac.tut.controller.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import za.ac.tut.model.User;
import za.ac.tut.model.bean.UserService;

public class LoginServlet extends HttpServlet {

    @EJB
    UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String errorMessage = "";
        User authenticatedUser = null;
        String resource = request.getParameter("resource");
        
        try {
            if (! userService.validateUsername(username)) {
                errorMessage = "Invalid username.";
            } else if (! userService.validatePassword(password)) {
                errorMessage = "Invalid password.";
            } else {
                authenticatedUser = userService.authenticateUser(username, password);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        if (authenticatedUser != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", authenticatedUser);

            if (resource != null) {
                response.sendRedirect(resource);
                return;
            }

            switch (authenticatedUser.getRoleId()) {
                case 1: // End User
                    response.sendRedirect("end_user_dashboard.jsp");
                    break;
                case 2: // Agent
                    response.sendRedirect("agent_dashboard.jsp");
                    break;
                case 3: // Technician
                    response.sendRedirect("technician_dashboard.jsp");
                    break;
                case 4: // Manager
                    response.sendRedirect("manager_dashboard.jsp");
                    break;
                default:
                    response.sendRedirect("login.jsp");
                    break;
            }
        } else {
            response.sendRedirect("login.jsp?error=" + errorMessage + 
                    (resource != null ? "&resource=" + resource : "") + 
                    "&username=" + username + "&password=" + password
            );
        }
    }

}
