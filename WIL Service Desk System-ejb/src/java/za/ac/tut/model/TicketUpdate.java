package za.ac.tut.model;

import java.util.Date;

public class TicketUpdate {

    private int updateId;
    private int ticketId;
    private User updatedBy;
    private String comment;
    private Date createdAt;
    private String ticketStatus;

    // Constructor
    public TicketUpdate(int updateId, int ticketId, int updatedBy, String comment, Date createdAt, String ticketStatus) {
        this.updateId = updateId;
        this.ticketId = ticketId;
        this.updatedBy = new User(updatedBy);
        this.comment = comment;
        this.createdAt = createdAt;
        setTicketStatus(ticketStatus);
    }
    
    public TicketUpdate(int updateId, int ticketId, User updatedBy, String comment, Date createdAt, String ticketStatus) {
        this.updateId = updateId;
        this.ticketId = ticketId;
        this.updatedBy = updatedBy;
        this.comment = comment;
        this.createdAt = createdAt;
        setTicketStatus(ticketStatus);
    }

    public TicketUpdate(int ticketId, int updatedBy, String comment, String ticketStatus) {
        this.ticketId = ticketId;
        this.updatedBy = new User(updatedBy);
        this.comment = comment;
        setTicketStatus(ticketStatus);
    }
    
    

    // Getters and Setters
    public int getUpdateId() {
        return updateId;
    }

    public void setUpdateId(int updateId) {
        this.updateId = updateId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = new User(updatedBy);
    }
    
    public void setUpdatedBy(User updatedBy){
        this.updatedBy = updatedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public final void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    @Override
    public String toString() {
        return "TicketUpdate{" + "updatedBy=" + updatedBy + ", comment=" + comment + ", createdAt=" + createdAt + ", ticketStatus=" + ticketStatus + '}';
    }
    
    
}
