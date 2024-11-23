<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>
    <head>
        <title>Create Account</title>
        <link rel="stylesheet" href="styles.css">
    </head>
    <body>
        <header>
            <h2>Create Account</h2>
        </header>

        <!-- Account Creation Form -->
        <form action="CreateAccountServlet" method="post">
            
            <!-- Username Field -->
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required><br>

            <!-- Password Field -->
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required><br>

            <!-- Full Name Field -->
            <label for="fullName">Full Name:</label>
            <input type="text" id="fullName" name="fullName" required><br>

            <!-- Email Field -->
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required><br>

            <!-- Role Selection with Default set to End User -->
            <label for="role">Role:</label>
            <select id="role" name="role" required>
                <option value="1" selected>End User</option> <!-- End User is set as the default -->
            </select><br>

            <!-- Submit Button -->
            <button type="submit">Create Account</button>
        </form>

        <!-- Footer -->
        <footer>
            <p>&copy; <%= LocalDate.now().getYear() %> IT Service Management System</p>
        </footer>
    </body>
</html>
