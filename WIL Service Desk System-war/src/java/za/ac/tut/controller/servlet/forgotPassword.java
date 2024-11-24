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
import za.ac.tut.model.bean.UserService;

public class forgotPassword extends HttpServlet {

    @EJB
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get parameters from the form
        String usernameOrEmail = request.getParameter("usernameOrEmail");
        String newUsername = request.getParameter("username");
        String confirmUsername = request.getParameter("confirmUsername");
        String newPassword = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Ensure at least one field is filled out (either password or username)
        if ((newUsername == null || newUsername.trim().isEmpty()) && (newPassword == null || newPassword.trim().isEmpty())) {
            response.sendRedirect("forgot_password.jsp?error=Please enter either a new username or a new password.");
            return;
        }

        try {
            // Check if the username or email exists
            boolean userExists = userService.checkUserExists(usernameOrEmail);

            if (userExists) {
                boolean updateSuccessful = false;

                // Check if the username should be updated
                if (newUsername != null && !newUsername.isEmpty()) {
                    if (!newUsername.equals(confirmUsername)) {
                        response.sendRedirect("forgot_password.jsp?error=Usernames do not match.");
                        return;
                    }
                    // Update the username in the database
                    updateSuccessful = userService.updateUsername(usernameOrEmail, newUsername);
                }

                // Check if the password should be updated
                if (newPassword != null && !newPassword.isEmpty()) {
                    if (!newPassword.equals(confirmPassword)) {
                        response.sendRedirect("forgot_password.jsp?error=Passwords do not match.");
                        return;
                    }
                    // Update the password in the database
                    updateSuccessful = userService.updatePassword(usernameOrEmail, newPassword);
                }

                // If the update was successful, redirect to the login page
                if (updateSuccessful) {
                    response.sendRedirect("login-page?message=Your credentials have been updated successfully.");
                } else {
                    response.sendRedirect("forgot_password.jsp?error=An error occurred, please try again later.");
                }

            } else {
                response.sendRedirect("forgot_password.jsp?error=No user found with the provided username or email.");
            }
        } catch (SQLException e) {
            Logger.getLogger(forgotPassword.class.getName()).log(Level.SEVERE, null, e);
            response.sendRedirect("forgot_password.jsp?error=An error occurred, please try again later.");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(forgotPassword.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
