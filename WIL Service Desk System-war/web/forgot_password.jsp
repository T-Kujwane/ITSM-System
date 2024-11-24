<%-- 
    Document   : dashboard
    Created on : 15 Sept 2024, 02:00:59
    Author     : Thandeka Matampane
--%>

<%@page import="java.time.LocalDate"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Forgot Password or Username</title>
        <link rel="stylesheet" href="styles.css">
        <script>
            function validateForm() {
                var password = document.getElementById("password").value;
                var confirmPassword = document.getElementById("confirmPassword").value;
                var username = document.getElementById("username").value;
                var confirmUsername = document.getElementById("confirmUsername").value;

                // Check if both username and password fields are empty
                if (!username && !password) {
                    alert("If you have forgotten both your username and password, please contact the admin at <a href='mailto:Thandekabrad@gmail.com'>Thandekabrad@gmail.com</a>.");
                    return false;
                }

                // Validate password match
                if (password && confirmPassword && password !== confirmPassword) {
                    alert("Passwords do not match!");
                    return false;
                }

                // Validate username match
                if (username && confirmUsername && username !== confirmUsername) {
                    alert("Usernames do not match!");
                    return false;
                }

                return true;
            }
        </script>
    </head>
    <body>
        <header>
            <h2>Reset Password or Username</h2>
        </header>
        
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
        
        <footer>
            <p>&copy; <%= LocalDate.now().getYear() %> IT Service Management System</p>
        </footer>
    </body>
</html>
