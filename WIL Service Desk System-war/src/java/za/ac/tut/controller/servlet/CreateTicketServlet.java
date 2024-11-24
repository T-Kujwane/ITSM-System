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
import za.ac.tut.model.bean.TicketService;
import za.ac.tut.model.bean.UserService;
import za.ac.tut.model.bean.EmailService;

public class CreateTicketServlet extends HttpServlet {

    @EJB
    TicketService ticketService;

    @EJB
    EmailService emailService;

    @EJB
    private UserService userService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String assignedToStr = request.getParameter("assignedTo");
        int assignedTo = Integer.parseInt(assignedToStr == null? "1" : assignedToStr);
        
        Ticket ticket = new Ticket(title, description, Ticket.Status.OPEN.toString(), user.getUserId());

        Ticket createdTicket;

        try {
            if (assignedTo > 1){
                ticket.setAssignedTo(assignedTo);
            }
            
            createdTicket = ticketService.createTicket(ticket);
            
            // Send email notification
            if (createdTicket != null) {
                System.out.println("Ticket \"assigned to\" value: " + createdTicket.getAssignedTo());
                String emailSubject = "ITSM - New Ticket Created: " + title;
                String emailMessage = "A new ticket has been created\n"
                        + "Ticket ID: REP00000" + createdTicket.getTicketId() + "\n"
                        + "Title: " + title + "\n"
                        + "Description: " + description + "\n\n\n"
                        + "The assigned ticket is of Priority Level - " + createdTicket.getPriority().getPriorityLevel()
                        + " and should be resolved within " + createdTicket.getPriority().getSlaTime() + " hours.";
                
                if (createdTicket.getAssignedTo() != null && createdTicket.getAssignedTo().getUserId() > 1){
                    if (createdTicket.getAssignedTo().getEmail() != null && ! createdTicket.getAssignedTo().getEmail().equalsIgnoreCase("null") && ! createdTicket.getAssignedTo().getEmail().isEmpty()){
                        String technicianEmail = this.userService.getUserEmail(createdTicket.getAssignedTo().getUserId());
                        //Notify technician of the assigned ticket
                        emailService.sendEmail(technicianEmail, emailSubject, emailMessage);
                    }
                }
                
                //Notify creator that their ticket has been created
                emailMessage = "Dear " + user.getFullName() + "\n\n"
                        + "Thank you for reporting your technical challenge. We would like to assure"
                        + " you that our team is working on it, and the issue will be resolved in a "
                        + "timely manner. You encouraged to exercise patience during this time.\n"
                        + "Please take note that your assigned ticket reference number is REP00000" + createdTicket.getTicketId() + "\n"
                        + ". You are required to quote your reference number when reaching out to service desk regarding this matter.\n\n"
                        + "Thank you.\n\n"
                        + "Kind regards,\n"
                        + " IT Service Management";
                emailService.sendEmail(user.getEmail(), emailSubject, emailMessage);
            }
        } catch (ClassNotFoundException | SQLException | MessagingException ex) {
            Logger.getLogger(CreateTicketServlet.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        String msg = createdTicket != null ? "Ticket created successfully." : "Failed to create ticket.";
        String url;

        switch (user.getRoleId()) {
            case 1:
                url = "user";
                break;
            case 2:
                url = "agent";
                break;
            case 3:
                url = "technician";
                break;
            default:
                url = "manager";
        }

        response.sendRedirect(url + "?msg=" + msg);
    }
}
