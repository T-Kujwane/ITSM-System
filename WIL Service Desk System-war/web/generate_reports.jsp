<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Download Report</title>
        <link rel="stylesheet" href="styles.css"> <!-- Add your CSS file -->
    </head>
    <body>
        <!-- Header with Navigation Links -->
        <header>
            <div>
                <h1>Service Desk System</h1>
                <nav>
                    <ul>
                        <li><a href="logout.jsp">Dashboard</a></li>
                    </ul>
                </nav>
            </div>
        </header>

        <div class="container">
            <h2>Download Ticket Report</h2>

            <!-- Form to choose report type (PDF or CSV) -->
            <form method="GET" action="ReportDownloadServlet">
                <label for="reportType">Select Report Type:</label>
                <select name="reportType" id="reportType">
                    <option value="pdf">PDF</option>
                    <option value="csv">CSV</option>
                </select>
                <button type="submit">Download Report</button>
            </form>
        </div>

        <footer>
            <p>&copy; 2024 Service Desk System. All rights reserved.</p>
        </footer>
    </body>
</html>
