package db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static DBConnector dbConnector;
    private Connection connection;
    private DBConnector(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
    public static DBConnector getInstance(){
        return (dbConnector == null) ? dbConnector = new DBConnector() : dbConnector;
    }
    public Connection getConnection(){
        return connection;
    }
}
