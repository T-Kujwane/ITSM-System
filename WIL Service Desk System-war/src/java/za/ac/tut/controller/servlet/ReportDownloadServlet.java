package za.ac.tut.controller.servlet;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
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
        String reportType = request.getParameter("reportType"); // Get report type from request
        
        if ("pdf".equals(reportType)) {
            generatePdfReport(response);
        } else if ("csv".equals(reportType)) {
            generateCsvReport(response);
        } else {
            response.getWriter().write("Invalid report type selected.");
        }
    }
    
    private void generatePdfReport(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");

        try (OutputStream out = response.getOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Add a title to the document
            document.add(new Paragraph("Ticket Report"));
            document.add(new Paragraph(" ")); // Empty line
            
            Class.forName("com.mysql.jdbc.Driver"); // Use the appropriate driver
            String jdbcUrl = "jdbc:mysql://localhost:3306/service_desk_system?useSSL=false";
            Connection connection = DriverManager.getConnection(jdbcUrl, "root", "root");
            Statement statement = connection.createStatement();
            String query = "SELECT ticket_id, title, status FROM tickets"; // Adjust based on what you need
            ResultSet resultSet = statement.executeQuery(query);
    
            while (resultSet.next()) {
                String row = String.format("Ticket ID: %d, Title: %s, Status: %s",
                        resultSet.getInt("ticket_id"),
                        resultSet.getString("title"),
                        resultSet.getString("status"));
                document.add(new Paragraph(row));
            }
    
            document.close();
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException | DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getOutputStream().write(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    private void generateCsvReport(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            // Database connection
            Class.forName("com.mysql.jdbc.Driver"); // Use the correct driver
            String jdbcUrl = "jdbc:mysql://localhost:3306/service_desk_system?useSSL=false";
            try (Connection connection = DriverManager.getConnection(jdbcUrl, "root", "root")) {
                Statement statement = connection.createStatement();
                String query = "SELECT ticket_id, title, status FROM tickets"; // Adjust as needed
                ResultSet resultSet = statement.executeQuery(query);
                
                // Write CSV header
                writer.println("Ticket ID,Title,Status");
                while (resultSet.next()) {
                    writer.printf("%d,%s,%s%n",
                            resultSet.getInt("ticket_id"),
                            resultSet.getString("title"),
                            resultSet.getString("status"));
                }
                
                resultSet.close();
                statement.close();
            }
        } catch (SQLException | ClassNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to generate CSV: " + e.getMessage());
        }
    }
}