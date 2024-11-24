package za.ac.tut.controller.servlet;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReportDownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reportType = request.getParameter("reportType");
        
        try {
            if ("pdf".equalsIgnoreCase(reportType)) {
                generatePdfReport(response);
            } else if ("csv".equalsIgnoreCase(reportType)) {
                generateCsvReport(response);
            } else {
                response.getWriter().write("Invalid report type selected.");
            }
        } catch (Exception e) {
            response.getWriter().write("Error generating report: " + e.getMessage());
        }
    }
private void generatePdfReport(HttpServletResponse response) throws IOException, DocumentException {
    response.setContentType("application/pdf");
    response.setHeader("Content-Disposition", "attachment; filename=\"ticket_report.pdf\"");

    try (OutputStream out = response.getOutputStream()) {
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        // Add title
        document.add(new Paragraph("Ticket Report"));
        document.add(new Paragraph(" "));

        // Create a table with columns
        PdfPTable table = new PdfPTable(5); // Number of columns should match the query result
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);

        // Add table headers
        table.addCell("Ticket ID");
        table.addCell("Title");
        table.addCell("Status");
        table.addCell("Priority");
        table.addCell("Assigned Technician");

        // Fetch ticket data from the database
        Class.forName("com.mysql.cj.jdbc.Driver");
        String jdbcUrl = "jdbc:mysql://localhost:3306/service_desk_system?useSSL=false";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "root");
             Statement statement = connection.createStatement()) {

            // Corrected query
            String query = "SELECT t.ticket_id, t.title, t.status, p.priority_name, u.full_name " +
                           "FROM tickets t " +
                           "JOIN ticket_priority tp ON t.ticket_id = tp.ticket_id " +
                           "JOIN priority p ON tp.priority_id = p.priority_id " +
                           "JOIN users u ON t.assigned_to = u.user_id";
            ResultSet resultSet = statement.executeQuery(query);

            // Add ticket data to table
            while (resultSet.next()) {
                table.addCell(String.valueOf(resultSet.getInt("ticket_id")));
                table.addCell(resultSet.getString("title"));
                table.addCell(resultSet.getString("status"));
                table.addCell(resultSet.getString("priority_name"));
                table.addCell(resultSet.getString("full_name"));
            }

            // Add table to document
            document.add(table);
        } catch (SQLException e) {
            document.add(new Paragraph("Error fetching data: " + e.getMessage()));
        } finally {
            document.close();
        }
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getOutputStream().write(("Error generating PDF: " + e.getMessage()).getBytes());
    }
}
private void generateCsvReport(HttpServletResponse response) throws IOException {
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=\"ticket_report.csv\"");

    try (PrintWriter writer = response.getWriter()) {
        // Database connection and query execution
        Class.forName("com.mysql.cj.jdbc.Driver");
        String jdbcUrl = "jdbc:mysql://localhost:3306/service_desk_system?useSSL=false";
        
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "root")) {
            Statement statement = connection.createStatement();
            
            // Corrected SQL query to include priority_name from priority table
            String query = "SELECT t.ticket_id, t.title, t.status, p.priority_name, u.full_name " +
                           "FROM tickets t " +
                           "JOIN ticket_priority tp ON t.ticket_id = tp.ticket_id " +
                           "JOIN priority p ON tp.priority_id = p.priority_id " + // Correct join
                           "JOIN users u ON t.assigned_to = u.user_id";         // Join with users table for technician name
            
            ResultSet resultSet = statement.executeQuery(query);

            // Write CSV header
            writer.println("Ticket ID,Title,Status,Priority,Assigned Technician");

            // Write data rows
            while (resultSet.next()) {
                writer.printf("%d,%s,%s,%s,%s%n",
                        resultSet.getInt("ticket_id"),
                        resultSet.getString("title"),
                        resultSet.getString("status"),
                        resultSet.getString("priority_name"), // Correct column reference
                        resultSet.getString("full_name"));
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to generate CSV: " + e.getMessage());
        }
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("Failed to write CSV: " + e.getMessage());
    }
}
}