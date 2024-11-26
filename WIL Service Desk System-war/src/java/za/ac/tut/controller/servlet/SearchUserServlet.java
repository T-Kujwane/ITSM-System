package za.ac.tut.controller.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
public class SearchUserServlet extends HttpServlet {
    @EJB
    private UserService userService;
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String caller = request.getParameter("caller");
        String query = request.getParameter("query");
        User usr;
        
        try {
            usr = this.userService.getUserByEmail(query);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(SearchUserServlet.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        if (usr == null){
            try {
                usr = this.userService.getUserByFullName(query);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(SearchUserServlet.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }
        
        String msg = "User ".concat(query.concat(" found!!!"));
        if (usr == null){
            msg = "User ".concat(query.concat(" was not found or does not exist!!!"));
        }else {
            request.getSession(false).setAttribute("searchUser", usr);
        }
        
        response.sendRedirect(caller.concat("?msg=".concat(msg)));
    }

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
        doGet(request, response);
    }
}
