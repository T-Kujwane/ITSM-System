package za.ac.tut.model.bean;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import za.ac.tut.model.Ticket;
import za.ac.tut.model.TicketPriority;
import za.ac.tut.model.User;
import za.ac.tut.model.util.DBConnection;

@Stateless
public class TicketServiceBean implements TicketService {

    // Create Ticket (used by End Users)
    @Override
    public Ticket createTicket(Ticket ticket) throws ClassNotFoundException, SQLException {
        Ticket createdTicket = null;
        Connection conn = DBConnection.getConnection();
        String query;

        if (ticket.getAssignedTo() > 0) {
            query = "INSERT INTO tickets (title, description, created_by, assigned_to) VALUES (?, ?, ?, ?)";
        } else {
            query = "INSERT INTO tickets (title, description, created_by) VALUES (?, ?, ?)";
        }

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, ticket.getTitle());
        ps.setString(2, ticket.getDescription());
        ps.setInt(3, ticket.getCreatedBy());

        if (ticket.getAssignedTo() > 0) {
            ps.setInt(4, ticket.getAssignedTo());
        }

        int result = ps.executeUpdate();
        
        if (result > 0) {
            ResultSet rs = conn.prepareStatement("SELECT * FROM tickets ORDER BY ticket_id DESC LIMIT 1").executeQuery();
            if (rs.isBeforeFirst()) {
                rs.next();
                createdTicket = getTicket(rs);
                
                boolean isSuccessful = conn.prepareStatement("INSERT INTO ticket_priority (ticket_id, priority_id) VALUES(" + createdTicket.getTicketId() + ", (SELECT priority_id FROM priority WHERE LOWER(priority_name) = 'low'))").executeUpdate() > 0;
                if (isSuccessful){
                    ResultSet priorityResult = conn.prepareStatement("SELECT priority_name, sla_time FROM priority WHERE priority_id = (SELECT priority_name FROM ticket_priority WHERE ticket_id = " + createdTicket.getTicketId() + ")").executeQuery();
                    
                    if (priorityResult.isBeforeFirst()){
                        priorityResult.next();
                        String priorityLevel = priorityResult.getString("priority_name");
                        Integer slaTime = priorityResult.getInt("sla_time");
                        
                        createdTicket.getPriority().setPriorityLevel(priorityLevel);
                        createdTicket.getPriority().setSlaTime(slaTime);
                    }
                }
            }
        }
        
        return createdTicket;
    }

    // Update Ticket (used by Service Desk Agents and Technicians)
    @Override
    public boolean updateTicketStatus(int ticketId, String status, int updatedBy, String comment) throws ClassNotFoundException, SQLException {
        boolean isUpdated = false;
        Connection conn = DBConnection.getConnection();
        // Update the ticket status
        String updateQuery = "UPDATE tickets SET status = ? WHERE ticket_id = ?";
        PreparedStatement updatePs = conn.prepareStatement(updateQuery);
        updatePs.setString(1, status);
        updatePs.setInt(2, ticketId);
        int updateResult = updatePs.executeUpdate();

        // Log the update in the ticket_updates table
        String logQuery = "INSERT INTO ticket_updates (ticket_id, updated_by, comment) VALUES (?, ?, ?)";
        PreparedStatement logPs = conn.prepareStatement(logQuery);
        logPs.setInt(1, ticketId);
        logPs.setInt(2, updatedBy);
        logPs.setString(3, comment);
        int logResult = logPs.executeUpdate();

        if (updateResult > 0 && logResult > 0) {
            isUpdated = true;
        }

        return isUpdated;
    }

    // Get Tickets for a specific user (end user or technician)
    @Override
    public List<Ticket> getTicketsByUser(int userId) throws ClassNotFoundException, SQLException {
        List<Ticket> tickets = new ArrayList<>();

        String query = "SELECT * FROM tickets WHERE created_by = ? OR assigned_to = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(query);
        ps.setInt(1, userId);
        ps.setInt(2, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            tickets.add(getTicket(rs));
        }

        return tickets;
    }

    @Override
    public List<Ticket> getTicketsByStatus(String status) throws SQLException, ClassNotFoundException {
        List<Ticket> tickets = new ArrayList<>();

        ResultSet rs = DBConnection.getConnection().prepareStatement("SELECT * FROM tickets WHERE LOWER(status) = \'" + status.toLowerCase() + "\'").executeQuery();
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                tickets.add(getTicket(rs));
            }
        }
        return tickets;
    }

    private Ticket getTicket(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getInt("ticket_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("status"),
                rs.getInt("created_by"),
                rs.getInt("assigned_to"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }

    @Override
    public List<Ticket> getAllTickets() throws SQLException, ClassNotFoundException {
        ResultSet rs = DBConnection.getConnection().prepareStatement("SELECT * FROM tickets").executeQuery();
        List<Ticket> tickets = new ArrayList<>();
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                tickets.add(getTicket(rs));
            }
            
            for (Ticket t : tickets){
                ResultSet prioritiesResults = DBConnection.getConnection().prepareStatement("SELECT priority_name, sla_time FROM priority WHERE priority_id = (SELECT priority_id FROM ticket_priority WHERE ticket_id = " + t.getTicketId() + ")").executeQuery();
                if (prioritiesResults.isBeforeFirst()){
                    prioritiesResults.next();
                    
                    String priorityLevel = prioritiesResults.getString("priority_name");
                    Integer prioritySLA = prioritiesResults.getInt("sla_time");
                    
                    t.getPriority().setSlaTime(prioritySLA);
                    t.getPriority().setPriorityLevel(priorityLevel);
                    
                }
                prioritiesResults.close();
            }
        }
        return tickets;
    }

    @Override
    public int getTotalTickets() throws SQLException, ClassNotFoundException {
        return this.getAllTickets().size();
    }

    @Override
    public List<Ticket> getTicketsByPriority(String priority) throws SQLException, ClassNotFoundException {
        List<Ticket> tickets = new ArrayList<>();  // List to hold the tickets matching the priority

        /*// SQL query to fetch tickets by priority name
        String query = "SELECT t.* "
                + "FROM tickets t "
                + "JOIN ticket_priorities tp ON t.ticket_id = tp.ticket_id "
                + "JOIN priorities p ON tp.priority_id = p.priority_id "
                + "WHERE p.priority_name = ?";

        // Use try-with-resources to manage the database resources (Connection, PreparedStatement, ResultSet)
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the parameter (priority)
            stmt.setString(1, priority);

            // Execute the query
            try ( ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Map the result set to a Ticket object
                    Ticket ticket = new Ticket();
                    ticket.setTicketId(rs.getInt("ticket_id"));
                    ticket.setTitle(rs.getString("title"));
                    ticket.setDescription(rs.getString("description"));
                    ticket.setStatus(rs.getString("status"));
                    ticket.setCreatedBy(rs.getInt("created_by"));
                    ticket.setAssignedTo(rs.getInt("assigned_to"));
                    ticket.setCreatedAt(rs.getTimestamp("created_at"));
                    ticket.setUpdatedAt(rs.getTimestamp("updated_at"));
                    // Add the ticket to the list
                    tickets.add(ticket);
                }
            }
        }
         */
        return tickets;
    }

    @Override
    public Ticket getTicketById(int ticketId) throws SQLException, ClassNotFoundException {
        Ticket ticket = null;
        String query = "SELECT t.*, p.priority_name, tp.priority_id "
                + "FROM tickets t "
                + "LEFT JOIN ticket_priorities tp ON t.ticket_id = tp.ticket_id "
                + "LEFT JOIN priorities p ON tp.priority_id = p.priority_id "
                + "WHERE t.ticket_id = ?";

        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, ticketId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Creating a TicketPriority instance (you need to adjust according to your constructors)
                TicketPriority priority = new TicketPriority(
                        ticketId, // Assuming you pass ticketId to the priority
                        rs.getInt("priority_id"),
                        rs.getString("priority_name"),
                        0 // Set default SLA time or fetch from your priorities table
                );

                // Creating the Ticket instance
                ticket = new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getInt("created_by"),
                        rs.getInt("assigned_to"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                ticket.setPriority(priority); // Set the priority for the ticket
            }
        }

        return ticket;
    }

    @Override
    public boolean updateTicketStatus(int ticketId, String title, String description, String status, String priority) throws ClassNotFoundException, SQLException {
        int affectedRows = DBConnection.getConnection().prepareStatement("UPDATE tickets SET title = \'" + title + "\', description = \'" + description + "\', status = \'" + status + "\' WHERE ticket_id = " + ticketId ).executeUpdate();
        
        if (affectedRows > 0){
            affectedRows = DBConnection.getConnection().prepareStatement("UPDATE ticket_priority SET priority_id = (SELECT priority_id FROM priority WHERE LOWER(priority_name) = \'" + priority.toLowerCase() + "\') WHERE ticket_id = " + ticketId).executeUpdate();
            
            return affectedRows > 0;
        }
        
        return false;
    }

}
