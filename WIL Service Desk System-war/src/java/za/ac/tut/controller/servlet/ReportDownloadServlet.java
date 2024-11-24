package za.ac.tut.controller.servlet;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import za.ac.tut.model.User;
import za.ac.tut.model.bean.TicketService;

public class ReportDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reportType = request.getParameter("reportType");

        // Ensure user is logged in and has the appropriate role
        User usr = (User) request.getSession().getAttribute("user");
        if (usr == null || usr.getRoleId() != 4) { // role_id 4 for manager
            response.sendRedirect("login.jsp?resource=download_report.jsp");
            return;
        }

        // EJB Lookup for TicketService
        TicketService ticketService;
        try {
            Context ctx = new InitialContext();
            ticketService = (TicketService) ctx.lookup("java:global/WIL_Service_Desk_System/WIL_Service_Desk_System-ejb/TicketServiceBean!za.ac.tut.model.bean.TicketService");
        } catch (Exception e) {
            throw new ServletException("Failed to look up EJB: " + e.getMessage(), e);
        }

        // Generate appropriate report
        try {
            if ("pdf".equalsIgnoreCase(reportType)) {
                generatePdfReport(response, ticketService);
            } else if ("csv".equalsIgnoreCase(reportType)) {
                generateCsvReport(response, ticketService);
            } else {
                response.getWriter().write("Invalid report type selected.");
            }
        } catch (Exception e) {
            response.getWriter().write("Error generating report: " + e.getMessage());
        }
    }

    private void generatePdfReport(HttpServletResponse response, TicketService ticketService) throws IOException, DocumentException, SQLException, ClassNotFoundException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"ticket_report.pdf\"");

        Document document = new Document();
        try {
            OutputStream out = response.getOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // Add title
            document.add(new Paragraph("Ticket Report", FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD)));
            document.add(new Paragraph(" "));  // Add some space

            // Declare variables for counts
            int totalTickets = ticketService.getTotalTickets();  // Total tickets
            Map<String, Integer> statusCount = new HashMap<>();
            Map<String, Integer> priorityCount = new HashMap<>();

            // Populate the counts
            statusCount.put("Closed", ticketService.getTicketsByStatus("Closed").size());
            statusCount.put("In Progress", ticketService.getTicketsByStatus("In Progress").size());
            statusCount.put("Resolved", ticketService.getTicketsByStatus("Resolved").size());
            statusCount.put("Open", ticketService.getTicketsByStatus("Open").size());

            priorityCount.put("Low", ticketService.getTicketsByPriority("Low").size());
            priorityCount.put("Medium", ticketService.getTicketsByPriority("Medium").size());
            priorityCount.put("High", ticketService.getTicketsByPriority("High").size());
            priorityCount.put("Critical", ticketService.getTicketsByPriority("Critical").size());

            // Create a table for the report
            PdfPTable table = new PdfPTable(2); // 2 columns
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Add header for total tickets
            PdfPCell cell = new PdfPCell(new Phrase("Summary Overview"));
            cell.setColspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.GREEN); // Set background color to green
            cell.setPadding(10);
            table.addCell(cell);

            // Add Total Tickets
            table.addCell("Total Tickets:");
            table.addCell(String.valueOf(totalTickets));

            // Tickets by Status header
            table.addCell("Tickets by Status:");
            table.addCell(""); // Empty cell for alignment

            for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
                table.addCell(entry.getKey() + ":");
                table.addCell(String.valueOf(entry.getValue()));
            }

            // Tickets by Priority header
            table.addCell("Tickets by Priority:");
            table.addCell(""); // Empty cell for alignment

            for (Map.Entry<String, Integer> entry : priorityCount.entrySet()) {
                table.addCell(entry.getKey() + ":");
                table.addCell(String.valueOf(entry.getValue()));
            }

            // Add the table to the document
            document.add(table);

        } catch (DocumentException | IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getOutputStream().write(("Error generating PDF: " + e.getMessage()).getBytes());
            e.printStackTrace();  // Log the stack trace to server logs for debugging
        } finally {
            // Properly close the document
            document.close();
        }
    }

    private void generateCsvReport(HttpServletResponse response, TicketService ticketService) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"ticket_report.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            // Title of the report
            writer.println("Ticket Report");
            writer.println();  // Blank line for separation

            // Total Tickets
            int totalTickets = ticketService.getTotalTickets();
            writer.println("Total Tickets:," + totalTickets);
            writer.println();  // Blank line for separation

            // Tickets by Status
            writer.println("Tickets by Status:");
            writer.println("Status,Count");  // Header for status counts

            Map<String, Integer> statusCount = new HashMap<>();
            statusCount.put("Closed", ticketService.getTicketsByStatus("Closed").size());
            statusCount.put("In Progress", ticketService.getTicketsByStatus("In Progress").size());
            statusCount.put("Resolved", ticketService.getTicketsByStatus("Resolved").size());
            statusCount.put("Open", ticketService.getTicketsByStatus("Open").size());

            // Write status counts
            for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
                writer.printf("%s,%d%n", entry.getKey(), entry.getValue());
            }
            writer.println();  // Blank line for separation

            // Tickets by Priority
            writer.println("Tickets by Priority:");
            writer.println("Priority,Count");  // Header for priority counts

            Map<String, Integer> priorityCount = new HashMap<>();
            priorityCount.put("High", ticketService.getTicketsByPriority("High").size());
            priorityCount.put("Medium", ticketService.getTicketsByPriority("Medium").size());
            priorityCount.put("Low", ticketService.getTicketsByPriority("Low").size());
            priorityCount.put("Critical", ticketService.getTicketsByPriority("Critical").size());

            // Write priority counts
            for (Map.Entry<String, Integer> entry : priorityCount.entrySet()) {
                writer.printf("%s,%d%n", entry.getKey(), entry.getValue());
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to write CSV: " + e.getMessage());
        }
    }
}
