package za.ac.tut.controller.servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import za.ac.tut.model.Ticket;
import za.ac.tut.model.bean.TicketService;
import za.ac.tut.model.User;
import javax.servlet.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketManagementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Check if the user is logged in
            User usr = (User) request.getSession().getAttribute("user");
            if (usr == null) {
                response.sendRedirect("login.jsp?resource=manage_ticket.jsp");
                return;
            }
            
            // Only allow managers or technicians to manage tickets
            if (usr.getRoleId() != 4 && usr.getRoleId() != 3) {
                response.sendRedirect("forbidden.jsp");
                return;
            }
            
            // Get the ticket ID from the request
            String ticketIdStr = request.getParameter("ticketId");
            if (ticketIdStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ticket ID is required");
                return;
            }
            int ticketId = Integer.parseInt(ticketIdStr);
            
            // Lookup TicketService EJB
            InitialContext ctx = new InitialContext();
            TicketService ticketService = (TicketService) ctx.lookup("java:global/service_desk_system/TicketServiceBean!za.ac.tut.model.bean.TicketService");

            // Get ticket details from TicketService
            Ticket ticket = ticketService.getTicketById(ticketId);  // Assuming this method exists
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return;
            }

            // Set ticket as a request attribute for the JSP
            request.setAttribute("ticket", ticket);
            
            // If the user is a manager, get technicians for assignment
            if (usr.getRoleId() == 4) {  // Manager role
                List<User> technicians = ticketService.getTechnicians();
                request.setAttribute("technicians", technicians);
            }

            // Forward to JSP page to render the ticket details
            RequestDispatcher dispatcher = request.getRequestDispatcher("manage_ticket.jsp");
            dispatcher.forward(request, response);
        } catch (NamingException ex) {
            Logger.getLogger(TicketManagementServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(TicketManagementServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TicketManagementServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

       
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // This can handle form submissions like updating the status or assigning the ticket
        String action = request.getParameter("action");
        try {
            if ("updateStatus".equals(action)) {
                int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                String status = request.getParameter("status");
                
                // Lookup TicketService EJB
                InitialContext ctx = new InitialContext();
                TicketService ticketService = (TicketService) ctx.lookup("java:global/service_desk_system/TicketServiceBean!za.ac.tut.model.bean.TicketService");

                // Update ticket status
                boolean success = ticketService.updateTicketStatus(ticketId, status, /*updatedBy*/ 1, /*comment*/ "Updated by manager");
                
                if (success) {
                    response.sendRedirect("manage_ticket.jsp?ticketId=" + ticketId);
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update ticket status");
                }
            } else if ("assignTicket".equals(action)) {
                int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                int assignedTo = Integer.parseInt(request.getParameter("assignedTo"));
                
                // Lookup TicketService EJB
                InitialContext ctx = new InitialContext();
                TicketService ticketService = (TicketService) ctx.lookup("java:global/service_desk_system/TicketServiceBean!za.ac.tut.model.bean.TicketService");

                // Assign ticket to technician
                boolean success = ticketService.assignTicket(ticketId, assignedTo);
                
                if (success) {
                    response.sendRedirect("manage_ticket.jsp?ticketId=" + ticketId);
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to assign ticket");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error handling form submission");
        }
    }
}
