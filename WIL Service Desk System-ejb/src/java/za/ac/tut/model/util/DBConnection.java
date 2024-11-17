/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.ac.tut.model.util;
import java.sql.*;

/**
 *
 * @author Thato Keith Kujwane
 */
public class DBConnection {
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3360/service_desk_system?user=root&useSSL=false&allowPublicKeyRetrieval=true", "root", "root");
    }
}
