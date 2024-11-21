package za.ac.tut.controller.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import za.ac.tut.model.Ticket;
import za.ac.tut.model.User;
import za.ac.tut.model.bean.EmailService;
import za.ac.tut.model.bean.TicketService;

public class UpdateTicketServlet extends HttpServlet {

    @EJB
    private TicketService ticketService;

    @EJB
    private EmailService emailService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user != null) {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String status = request.getParameter("status");
            String priority = request.getParameter("priority");
            Integer assignedTo = Integer.parseInt(request.getParameter("assignedTo"));
            boolean isSuccessful;
            String msg = "Failed to update record";

            Ticket assignedTicket = null;

            try {
                isSuccessful = this.ticketService.updateTicketStatus(ticketId, title, description, status, priority, assignedTo);
                if (isSuccessful) {
                    assignedTicket = this.ticketService.getTicketById(ticketId);

                    msg = "Record sucessfully updated!";

                    if (assignedTo > 1) {
                        String technicianEmail = assignedTicket.getAssignedTo().getUserId() > 1 ? assignedTicket.getAssignedTo().getEmail() : "";

                        if (!technicianEmail.isEmpty()) {
                            String emailSubject = "ITSM - New Ticket Created: " + title;
                            String emailMessage = "Dear " + assignedTicket.getAssignedTo().getFullName()  + ",\n\n"
                                    + "Please note that you have been allocated a new ticket with the below information.\n"
                                    + "Ticket ID: REP00000" + ticketId + "\n"
                                    + "Title: " + title + "\n"
                                    + "Description: " + description + "\n\n\n"
                                    + "The assigned ticket is of Priority Level - " + assignedTicket.getPriority().getPriorityLevel()
                                    + " and should be resolved within " + assignedTicket.getPriority().getSlaTime() + " hours.\n\n"
                                    + "Kind regards, \n"
                                    + "IT Service Desk\n"
                                    + "WOC316D - Work Integrated Learning\n"
                                    + "Tshwane University of Technology";
                            try {
                                //Notify technician of the assigned ticket
                                emailService.sendEmail(technicianEmail, emailSubject, emailMessage);
                            } catch (MessagingException ex) {
                                Logger.getLogger(UpdateTicketServlet.class.getName()).log(Level.SEVERE, null, ex);
                                msg = "Ticket updated and assigned but failed to notify asigned personnel.";
                            }
                        }
                    }
                }
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(UpdateTicketServlet.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }

            String caller = request.getParameter("caller");

            response.sendRedirect(caller.concat("?msg=").concat(msg));
        }
    }
}
