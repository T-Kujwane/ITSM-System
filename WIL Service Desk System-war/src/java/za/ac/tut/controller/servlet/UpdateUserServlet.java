package za.ac.tut.controller.servlet;

import za.ac.tut.model.bean.TicketServiceBean;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;


public class UpdateUserServlet extends HttpServlet {

    private TicketServiceBean ticketService = new TicketServiceBean();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the ticket ID, status, comment, and updatedBy user from the request
        int ticketId = Integer.parseInt(request.getParameter("ticketId"));
        String status = request.getParameter("status");
        String comment = request.getParameter("comment");
        int updatedBy = Integer.parseInt(request.getParameter("updatedBy"));

        // Call the updateTicketStatusAndComment method from the service
        boolean isUpdated = false;
        try {
            isUpdated = ticketService.updateTicketStatusAndComment(ticketId, status, comment, updatedBy);
        } catch (SQLException | ClassNotFoundException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while updating the ticket.");
            return;
        }

        // Check the result and send appropriate response
        if (isUpdated) {
            response.setStatus(HttpServletResponse.SC_OK);  // HTTP 200
            response.getWriter().write("Ticket updated successfully.");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);  // HTTP 400
            response.getWriter().write("Failed to update the ticket.");
        }
    }
}
