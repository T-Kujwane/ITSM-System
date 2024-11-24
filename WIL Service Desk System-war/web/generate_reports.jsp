<%-- 
    Document   : dashboard
    Created on : 15 Sept 2024, 02:00:59
    Author     : Thandeka Matampane
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Download Report</title>
    <link rel="stylesheet" href="styles.css"> <!-- Add your CSS file if needed -->
</head>
<body>
    <div>
        <h1>Download Ticket Report</h1>
        
        <!-- Link to Manager Dashboard -->
        <p><a href="manager_dashboard.jsp">Dashboard</a></p>

        <form method="GET" action="ReportDownloadServlet">
            <label for="reportType">Select Report Type:</label>
            <select name="reportType" id="reportType">
                <option value="pdf">PDF</option>
                <option value="csv">CSV</option>
            </select>
            <button type="submit">Download Report</button>
        </form>
    </div>
</body>
</html>