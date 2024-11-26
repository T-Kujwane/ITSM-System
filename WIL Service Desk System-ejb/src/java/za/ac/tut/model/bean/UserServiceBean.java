package za.ac.tut.model.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import za.ac.tut.model.Ticket;
import za.ac.tut.model.User;
import za.ac.tut.model.util.DBConnection;

@Stateless(mappedName = "UserService", name = "UserService")
public class UserServiceBean implements UserService {

    @Override
    public boolean createUser(User user) throws ClassNotFoundException, SQLException {
        boolean isCreated = false;
        Connection conn = DBConnection.getConnection();
        String query = "INSERT INTO users (username, password, full_name, email, role_id) VALUES (?, MD5(?), ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getPassword());  // Password should be hashed
        ps.setString(3, user.getFullName());
        ps.setString(4, user.getEmail());
        ps.setInt(5, user.getRoleId());
        int result = ps.executeUpdate();
        if (result > 0) {
            isCreated = true;
        }

        return isCreated;
    }

    @Override
    public User authenticateUser(String username, String password) throws SQLException, ClassNotFoundException {
        User user = null;

        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM users WHERE username = ? AND password = MD5(?)";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            user = new User(rs.getString("username"), rs.getString("password"), rs.getString("full_name"), rs.getString("email"), rs.getInt("role_id"));
            user.setUserId(rs.getInt("user_id"));
        }

        return user;
    }

    @Override
    public List<User> getUserByType(String type) throws ClassNotFoundException, SQLException {
        Connection connection = DBConnection.getConnection();
        PreparedStatement pst = connection.prepareStatement("SELECT u.full_name, u.user_id FROM users u, roles r WHERE u.role_id = r.role_id AND LOWER(r.role_name) = \'" + type.toLowerCase() + "\'");
        ResultSet rs = pst.executeQuery();

        List<User> users = new ArrayList<>();

        while (rs.next()) {
            users.add(new User(rs.getString("full_name"), rs.getInt("user_id")));
        }

        rs.close();
        pst.close();
        connection.close();

        return users;
    }

    @Override
    public User getUserByID(int userID) throws ClassNotFoundException, SQLException {
        ResultSet rs = DBConnection.getConnection().prepareStatement("SELECT * FROM users u WHERE u.user_id = " + userID).executeQuery();
        if (!rs.isBeforeFirst()) {
            return null;
        }

        rs.next();

        User user = getUser(rs);

        return user;
    }

    @Override
    public String getUserEmail(int userID) throws ClassNotFoundException, SQLException {
        User u = this.getUserByID(userID);

        return u != null ? u.getEmail() : null;
    }

    @Override
    public boolean validateUsername(String username) throws ClassNotFoundException, SQLException {
        String query = "SELECT user_id FROM users WHERE username = \'" + username + "\'";
        return DBConnection.getConnection().prepareStatement(query).executeQuery().isBeforeFirst();
    }

    @Override
    public boolean validatePassword(String password) throws ClassNotFoundException, SQLException {
        String query = "SELECT username FROM users WHERE password = MD5(\'" + password + "\')";
        return DBConnection.getConnection().prepareStatement(query).executeQuery().isBeforeFirst();
    }

    @Override
    public User getUserByEmail(String email) throws ClassNotFoundException, SQLException {
        ResultSet rs = DBConnection.getConnection().prepareStatement("SELECT * FROM users WHERE LOWER(email) = \'" + email.toLowerCase() + "\'").executeQuery();
        User user = null;

        if (rs.isBeforeFirst()) {
            rs.next();
            user = getUser(rs);
        }

        return user;
    }

    @Override
    public User getUserByFullName(String fullname) throws ClassNotFoundException, SQLException {
        ResultSet rs = DBConnection.getConnection().prepareStatement("SELECT * FROM users WHERE LOWER(full_name) LIKE \'%" + fullname.toLowerCase() + "%\'").executeQuery();
        User user = null;

        if (rs.isBeforeFirst()) {
            rs.next();
            user = getUser(rs);
        }

        return user;
    }

    private User getUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password"), rs.getString("full_name"), rs.getString("email"), rs.getInt("role_id"));
    }

    @Override
    public boolean deleteUser(int userID) throws SQLException, ClassNotFoundException {
        int rowsAffected = DBConnection.getConnection().prepareStatement("DELETE FROM users WHERE user_id = " + userID).executeUpdate();

        return rowsAffected > 0;
    }

    @Override
    public boolean updateUser(int userID, String username, String fullname, String email, int roleID) throws ClassNotFoundException, SQLException {
        String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, role_id = ? WHERE user_id = ?";
        PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql);

        stmt.setString(1, username);  // Set the username
        stmt.setString(2, fullname);  // Set the fullname
        stmt.setString(3, email);     // Set the email
        stmt.setInt(4, roleID);       // Set the role ID
        stmt.setInt(5, userID);       // Set the user ID for the WHERE clause

        // Execute the update statement
        return stmt.executeUpdate() > 0;

    }

    @Override
    public boolean checkUserExists(String usernameOrEmail) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usernameOrEmail); // Set the username or email parameter
            stmt.setString(2, usernameOrEmail); // Set the username or email parameter
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; // Check if the count is greater than 0
            }
        }
    }

    @Override
    public boolean updateUsername(String usernameOrEmail, String newUsername) throws SQLException, ClassNotFoundException {
        // First, find the user by the provided username or email
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("full_name"),
                            rs.getString("email"), rs.getInt("role_id"));
                }
            }
        }

        if (user != null) {
            // Now update the username
            String updateQuery = "UPDATE users SET username = ? WHERE user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, newUsername);
                stmt.setInt(2, user.getUserId());

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } else {
            // User not found
            return false;
        }
    }

    @Override
    public boolean updatePassword(String usernameOrEmail, String newPassword) throws SQLException, ClassNotFoundException {
        // First, find the user by the provided username or email
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("password"), rs.getString("full_name"),
                            rs.getString("email"), rs.getInt("role_id"));
                }
            }
        }

        if (user != null) {
            // Now update the password (MD5 hashing it as before)
            String updateQuery = "UPDATE users SET password = MD5(?) WHERE user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, newPassword);  // MD5 password update
                stmt.setInt(2, user.getUserId());

                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
        } else {
            // User not found
            return false;
        }
    }
}
