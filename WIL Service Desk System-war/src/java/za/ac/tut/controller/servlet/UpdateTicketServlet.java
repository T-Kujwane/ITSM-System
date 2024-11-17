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
import za.ac.tut.model.bean.TicketService;

public class UpdateTicketServlet extends HttpServlet {

    @EJB
    TicketService ticketService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        int ticketId = Integer.parseInt(request.getParameter("ticketId"));
        String status = request.getParameter("status");
        String comment = request.getParameter("comment");

        boolean isUpdated;
        
        try {
            isUpdated = ticketService.updateTicketStatus(ticketId, status, user.getUserId(), comment);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(UpdateTicketServlet.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        if (isUpdated) {
            response.sendRedirect("viewTickets.jsp");
        } else {
            response.sendRedirect("error.jsp");
        }
    }
}
