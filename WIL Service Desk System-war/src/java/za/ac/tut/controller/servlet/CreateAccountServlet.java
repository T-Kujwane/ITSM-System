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
import za.ac.tut.model.User;
import za.ac.tut.model.bean.UserService;

public class CreateAccountServlet extends HttpServlet {

    @EJB
    UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        int role = Integer.parseInt(request.getParameter("role"));

        try {
            if (userService.validateUsername(username)) {
                response.sendRedirect("create_account.jsp?error=Username already exists");
                return;
            }

            User newUser = new User(username, password, fullName, email, role);
            boolean created = userService.createUser(newUser);

            if (created) {
                response.sendRedirect("login.jsp");
            } else {
                response.sendRedirect("create_account.jsp?error=Account creation failed");
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(CreateAccountServlet.class.getName()).log(Level.SEVERE, null, ex);
            response.sendRedirect("create_account.jsp?error=An error occurred");
        }
    }
}
