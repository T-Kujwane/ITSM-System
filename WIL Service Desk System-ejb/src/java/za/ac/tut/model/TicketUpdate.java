package za.ac.tut.model;

import java.util.Date;

public class TicketUpdate {

    private int updateId;
    private int ticketId;
    private int updatedBy;
    private String comment;
    private Date createdAt;

    // Constructor
    public TicketUpdate(int updateId, int ticketId, int updatedBy, String comment, Date createdAt) {
        this.updateId = updateId;
        this.ticketId = ticketId;
        this.updatedBy = updatedBy;
        this.comment = comment;
        this.createdAt = createdAt;
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

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
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
}
