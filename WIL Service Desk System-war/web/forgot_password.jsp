<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Forgot Password or Username</title>
        <link rel="stylesheet" href="styles.css">

        <script>
            // Function to validate the form inputs before submission
            function validateForm() {
                // Get the form values
                var password = document.getElementById('password').value;
                var confirmPassword = document.getElementById('confirmPassword').value;
                var username = document.getElementById('username').value;
                var confirmUsername = document.getElementById('confirmUsername').value;

                // Validate password match
                if (password && confirmPassword && password !== confirmPassword) {
                    alert("Passwords do not match!");
                    return false; // Prevent form submission
                }

                // Validate username match
                if (username && confirmUsername && username !== confirmUsername) {
                    alert("Usernames do not match!");
                    return false; // Prevent form submission
                }

                return true; // Allow form submission if no issues
            }
        </script>
    </head>
    <body>
        <header>
            <h2>Reset Password or Username</h2>
        </header>

        <!-- Form for Password Reset -->
        <form action="forgotPassword" method="post" onsubmit="return validateForm()">
            <label>Enter Username or Email:</label>
            <input type="text" name="usernameOrEmail" required><br>

            <label>New Username (if changing username):</label>
            <input type="text" id="username" name="username"><br>

            <label>Confirm New Username:</label>
            <input type="text" id="confirmUsername" name="confirmUsername"><br>

            <label>New Password (if changing password):</label>
            <input type="password" id="password" name="password"><br>

            <label>Confirm New Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword"><br>

            <button type="submit">Submit</button>
        </form>

        <!-- Display Error or Success Message -->
        <%
            String errorMessage = request.getParameter("error");
            String successMessage = request.getParameter("message");
            if (errorMessage != null) {
        %>
                <div style="color: red;">
                    <%= errorMessage %>
                </div>
        <%
            }
            if (successMessage != null) {
        %>
                <div style="color: green;">
                    <%= successMessage %>
                </div>
        <%
            }
        %>

        <footer>
            <p>&copy; 2024 IT Service Management System</p>
        </footer>
    </body>
</html>
