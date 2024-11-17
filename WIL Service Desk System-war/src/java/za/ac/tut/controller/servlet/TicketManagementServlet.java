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
import javax.ejb.EJB;
import za.ac.tut.model.bean.UserService;

public class TicketManagementServlet extends HttpServlet {
    @EJB
    private UserService userService;
    
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
            Ticket ticket = ticketService.getTicketById(ticketId);
            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found");
                return;
            }

            // Set ticket as a request attribute for the JSP
            request.setAttribute("ticket", ticket);

            // If the user is a manager, get technicians for assignment
            if (usr.getRoleId() == 4) {  // Manager role
                List<User> technicians = userService.getUserByType("technician"); // This should be defined in TicketService
                request.setAttribute("technicians", technicians);
            }

            // Forward to JSP page to render the ticket details
            RequestDispatcher dispatcher = request.getRequestDispatcher("manage_ticket.jsp");
            dispatcher.forward(request, response);
        } catch (NamingException | SQLException | ClassNotFoundException ex) {
            Logger.getLogger(TicketManagementServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        try {
            InitialContext ctx = new InitialContext();
            TicketService ticketService = (TicketService) ctx.lookup("java:global/service_desk_system/TicketServiceBean!za.ac.tut.model.bean.TicketService");

            if ("updateStatus".equals(action)) {
                int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                String status = request.getParameter("status");

                // Assume you have a user ID for the person updating (from session or hardcoded for example)
                User usr = (User) request.getSession().getAttribute("user");
                int updatedById = usr.getUserId(); // Assuming User has getUserId() method

                // Update ticket status
                boolean success = ticketService.updateTicketStatus(ticketId, status, updatedById, "Updated by manager");
                if (success) {
                    response.sendRedirect("manage_ticket.jsp?ticketId=" + ticketId);
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to update ticket status");
                }
            } else if ("assignTicket".equals(action)) {
                int ticketId = Integer.parseInt(request.getParameter("ticketId"));
                int assignedTo = Integer.parseInt(request.getParameter("assignedTo"));

                // Assign ticket to technician
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error handling form submission");
        }
    }
}
