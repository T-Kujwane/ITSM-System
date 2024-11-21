package za.ac.tut.model.bean;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import za.ac.tut.model.Ticket;
import za.ac.tut.model.User;

import za.ac.tut.model.util.DBConnection;

@Stateless
public class TicketServiceBean implements TicketService {

    @EJB
    private UserService userService;

    // Create Ticket (used by End Users)
    @Override
    public Ticket createTicket(Ticket ticket) throws ClassNotFoundException, SQLException {
        Ticket createdTicket = null;
        Connection conn = DBConnection.getConnection();
        String query;

        if (ticket.getAssignedTo() != null && ticket.getAssignedTo().getUserId() > 1) {
            query = "INSERT INTO tickets (title, description, created_by, assigned_to) VALUES (?, ?, ?, ?)";
        } else {
            query = "INSERT INTO tickets (title, description, created_by) VALUES (?, ?, ?)";
        }

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, ticket.getTitle());
        ps.setString(2, ticket.getDescription());
        ps.setInt(3, ticket.getCreatedBy());

        if (ticket.getAssignedTo() != null && ticket.getAssignedTo().getUserId() > 1) {
            ps.setInt(4, ticket.getAssignedTo().getUserId());
        }

        int result = ps.executeUpdate();

        if (result > 0) {
            ResultSet rs = conn.prepareStatement("SELECT ticket_id FROM tickets ORDER BY ticket_id DESC LIMIT 1").executeQuery();
            if (rs.isBeforeFirst()) {
                rs.next();
                int ticketID = rs.getInt("ticket_id");

                boolean isSuccessful = conn.prepareStatement("INSERT INTO ticket_priority (ticket_id, priority_id) VALUES(" + ticketID + ", (SELECT priority_id FROM priority WHERE LOWER(priority_name) = 'low'))").executeUpdate() > 0;
                if (isSuccessful) {
                    createdTicket = getTicketById(ticketID);
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

    private Ticket getTicket(ResultSet rs) throws SQLException, SQLException, SQLException, SQLException {
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
    synchronized public List<Ticket> getAllTickets() throws SQLException, ClassNotFoundException {
        ResultSet rs = DBConnection.getConnection().prepareStatement("SELECT * FROM tickets").executeQuery();
        List<Ticket> tickets = new ArrayList<>();
        Map<Integer, Ticket> replacementTicketsMap = new HashMap<>();

        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                tickets.add(getTicket(rs));
            }

            for (Ticket t : tickets) {
                Ticket ticket = this.getTicketById(t.getTicketId());
                int ticketIndex = tickets.indexOf(t);
                replacementTicketsMap.put(ticketIndex, ticket);
            }

            tickets.clear();

            for (Map.Entry<Integer, Ticket> ticketReplacement : replacementTicketsMap.entrySet()) {
                tickets.add(ticketReplacement.getKey(), ticketReplacement.getValue());
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
        Connection conn = DBConnection.getConnection();

        ResultSet rs = conn.prepareStatement("SELECT * FROM tickets WHERE ticket_id = " + ticketId).executeQuery();
        if (rs.isBeforeFirst()) {
            rs.next();
            ticket = getTicket(rs);
            System.out.println("Selecting priority_id");
            //ResultSet priorityResult = conn.prepareStatement("SELECT p.priority_name, p.sla_time FROM priority p WHERE p.priority_id = (SELECT tp.priority__id FROM ticket_priority tp WHERE tp.ticket_id = " + ticket.getTicketId() + ")").executeQuery();
            ResultSet priorityResult = conn.prepareStatement("SELECT p.priority_name, p.sla_time FROM priority p, ticket_priority tp WHERE p.priority_id = tp.priority_id AND tp.ticket_id = " + ticket.getTicketId()).executeQuery();
            System.out.println("Done selecting priority_id");
            if (priorityResult.isBeforeFirst()) {
                priorityResult.next();
                String priorityLevel = priorityResult.getString("priority_name");
                Integer slaTime = priorityResult.getInt("sla_time");

                ticket.getPriority().setPriorityLevel(priorityLevel);
                ticket.getPriority().setSlaTime(slaTime);
            }

            priorityResult.close();
            System.out.println("Getting assigned technician for ticket " + ticketId);
            ResultSet assignedTechnicianRes = conn.prepareStatement("SELECT assigned_to FROM tickets WHERE ticket_id = " + ticket.getTicketId()).executeQuery();
            assignedTechnicianRes.next();

            int assignedTechnicianID = assignedTechnicianRes.getInt("assigned_to");
            System.out.println("Ticket " + ticketId + " is assigned to technician " + assignedTechnicianID);
            assignedTechnicianRes.close();

            User assignedTechnician = this.userService.getUserByID(assignedTechnicianID == 0 ? 1 : assignedTechnicianID);

            ticket.setAssignedTo(assignedTechnician);
        }

        return ticket;
    }

    @Override
    public boolean updateTicketStatus(int ticketId, String title, String description, String status, String priority, int assignedTO) throws ClassNotFoundException, SQLException {
        int affectedRows = DBConnection.getConnection().prepareStatement("UPDATE tickets SET title = \'" + title + "\', description = \'" + description + "\', status = \'" + status + "\', assigned_to = " + assignedTO + " WHERE ticket_id = " + ticketId).executeUpdate();

        if (affectedRows > 0) {
            affectedRows = DBConnection.getConnection().prepareStatement("UPDATE ticket_priority SET priority_id = (SELECT priority_id FROM priority WHERE LOWER(priority_name) = \'" + priority.toLowerCase() + "\') WHERE ticket_id = " + ticketId).executeUpdate();

            return affectedRows > 0;
        }

        return false;
    }
}
