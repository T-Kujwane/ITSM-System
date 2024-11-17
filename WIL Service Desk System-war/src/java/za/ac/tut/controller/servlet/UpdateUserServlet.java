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

/**
 *
 * @author Thato Keith Kujwane
 */
public class UpdateUserServlet extends HttpServlet {
    @EJB
    private UserService userService;
    
    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int userID = Integer.parseInt(request.getParameter("userID"));
        String caller = request.getParameter("caller");
        
        User usr = (User) request.getSession(false).getAttribute("searchUser");
        
        boolean isSuccessful = false;
        
        if (action.toLowerCase().contains("update")) {
            action = "Update";
            
            String username = request.getParameter("username");
            String fullname = request.getParameter("fullName");
            int roleID = Integer.parseInt(request.getParameter("roleId"));
            String email = request.getParameter("email");
            
            try {
                isSuccessful = this.userService.updateUser(userID, username, fullname, email, roleID);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(UpdateUserServlet.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        } else {
            action = "Delete";
            
            try {
                isSuccessful = this.userService.deleteUser(userID);
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(UpdateUserServlet.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        
        String msg = action.concat(isSuccessful ? " operation successfull" : " operation failed.");
        
        if (isSuccessful){
            usr = null;
        }
        
        System.out.println(msg + "\n\n" + "User value is " + usr);
        request.getSession(false).setAttribute("searchUser", usr);
        response.sendRedirect(caller.concat("?msg=".concat(msg)));
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
