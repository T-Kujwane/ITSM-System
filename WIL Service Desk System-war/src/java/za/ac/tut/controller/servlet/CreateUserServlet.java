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

public class CreateUserServlet extends HttpServlet {

    @EJB
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        int roleId = Integer.parseInt(request.getParameter("roleId"));

        User user = new User(username, password, fullName, email, roleId);
        boolean isCreated;
        
        try {
            isCreated = userService.createUser(user);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(CreateUserServlet.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        String msg = "Failed to create user!!";
        if (isCreated) {
            msg = "User created successfully.";
        }
        
        response.sendRedirect("manager_dashboard.jsp?msg=" + msg);
    }
}
