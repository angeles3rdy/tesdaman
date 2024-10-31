package config;

import java.sql.Connection;
import java.sql.DriverManager;
//Putanginamo //helloworld
public class DBConfig {

    private Connection conn;

    public Connection getConnection() {

        try {
            String driver = "com.mysql.cj.jdbc.Driver";
            String dbURL = "jdbc:mysql://localhost:3306/inventory_system";
            String user = "root";
            String password = "";

            Class.forName(driver);
            conn = DriverManager.getConnection(dbURL, user, password);
            // System.out.println("COnnected");
            return conn;

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
