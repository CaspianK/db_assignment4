package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    public static Connection getConnection() {

        String url = "jdbc:sqlserver://localhost;database=hw3;user=Caspian;password=Ati1959";
        try {
            return DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
