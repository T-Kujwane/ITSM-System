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
import za.ac.tut.model.bean.UserService;

public class UpdateTicketServlet extends HttpServlet {

    @EJB
    private TicketService ticketService;

    @EJB
    private EmailService emailService;
    
    @EJB
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String caller = request.getParameter("caller");

        if (user != null) {
            int ticketId = Integer.parseInt(request.getParameter("ticketId"));
            String status = request.getParameter("status");
            boolean isSuccessful;
            String msg = "Failed to update record";
            String action = request.getParameter("action");

            if (action.equalsIgnoreCase("update")) {
                try {
                    isSuccessful = this.ticketService.update(ticketId, request.getParameter("comment"), status.toLowerCase(), user.getUserId());
                    
                    if (isSuccessful){
                        msg = "Ticket updated successfully.";
                        User reporter = this.userService.getUserByID(this.ticketService.getTicketById(ticketId).getCreatedBy());
                        
                        String message = "Dear " + reporter.getFullName() + ", \n\n" 
                                + "ICT Would like to notify you that the status of ticket " + this.ticketService.getTicketById(ticketId).getUserTicketID() + " that you had reported "
                                + "through the IT Service Management System has been updated to " + status + ". We would like you to rest"
                                + " assured that our technicians are working very hard to resolve the issue.\n"
                                + "Thank you for your patience and understanding during this time.\n\n"
                                + "Kind regards, \n"
                                + "IT Service Management\n"
                                + "WOC316D - Work Integrated Learning\n"
                                + "Tshwane University of Technology";
                        this.emailService.sendEmail(reporter.getEmail(), "Ticket Status Changed - " + status, message);
                    }
                } catch (SQLException | ClassNotFoundException | MessagingException ex) {
                    Logger.getLogger(UpdateTicketServlet.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
            } else if (action.equalsIgnoreCase("assign")) {
                String title = request.getParameter("title");
                String description = request.getParameter("description");
                String priority = request.getParameter("priority");
                Integer assignedTo = Integer.parseInt(request.getParameter("assignedTo"));
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
                                String emailMessage = "Dear " + assignedTicket.getAssignedTo().getFullName() + ",\n\n"
                                        + "Please note that you have been allocated a new ticket with the below information.\n"
                                        + "Ticket ID: REP00000" + ticketId + "\n"
                                        + "Title: " + title + "\n"
                                        + "Description: " + description + "\n\n\n"
                                        + "The assigned ticket is of Priority Level - " + assignedTicket.getPriority().getPriorityLevel()
                                        + " and should be resolved within " + assignedTicket.getPriority().getSlaTime() + " hours.\n\n"
                                        + "Kind regards, \n"
                                        + "IT Service Management\n"
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
            }

            response.sendRedirect(caller.concat("?msg=").concat(msg));
        } else {
            response.sendRedirect("login-page?resource=".concat(caller));
        }
    }
}
