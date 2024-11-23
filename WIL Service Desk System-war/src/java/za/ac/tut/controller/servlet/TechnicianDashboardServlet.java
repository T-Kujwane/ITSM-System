package za.ac.tut.controller.servlet;

import za.ac.tut.model.Ticket;
import za.ac.tut.model.User;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TechnicianDashboardServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/service_desk_system"; // Adjust your DB URL
    private static final String DB_USER = "root"; // Replace with your DB username
    private static final String DB_PASSWORD = "root"; // Replace with your DB password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the logged-in user (technician) from the session
        User usr = (User) request.getSession().getAttribute("user");

        // If the user is not logged in, redirect to login page
        if (usr == null) {
            response.sendRedirect("login-page?resource=technician");
            return;
        }

        // Get the technician's user_id
        int technicianId = usr.getUserId();

        // Retrieve the assigned tickets for the technician
        List<Ticket> ticketList = getTicketsAssignedToTechnician(technicianId);

        // Set the ticket list as a request attribute
        request.setAttribute("ticketList", ticketList);

        // Forward the request to the technician dashboard JSP
        request.getRequestDispatcher("technician_dashboard.jsp").forward(request, response);
    }

    // This method fetches the tickets assigned to the technician from the database
    private List<Ticket> getTicketsAssignedToTechnician(int technicianId) {
        System.out.println("rtfghj");
        List<Ticket> tickets = new ArrayList<>();
        
        // Query to fetch tickets assigned to the technician, excluding CLOSED status
        String query = "SELECT t.ticket_id, t.title, t.description, t.status, t.created_by, t.created_at, t.updated_at, p.priority_name "
                     + "FROM tickets t "
                     + "JOIN ticket_priority tp ON t.ticket_id = tp.ticket_id "
                     + "JOIN priority p ON tp.priority_id = p.priority_id "
                     + "WHERE t.assigned_to = ? AND t.status != 'CLOSED'";

        // Database connection and query execution
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, technicianId);
            ResultSet rs = ps.executeQuery();

            // Process the result set
           while (rs.next()) {
                int ticketId = rs.getInt("ticket_id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String status = rs.getString("status");
                String priority = rs.getString("priority_name");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Timestamp updatedAt = rs.getTimestamp("updated_at");

                // Create a Ticket object and add it to the list
                Ticket ticket = new Ticket(ticketId, title, description, status, technicianId, technicianId, createdAt, updatedAt, priority);
                tickets.add(ticket);
            
            }

        } catch (SQLException e) {
        }

        return tickets;
    }
}
