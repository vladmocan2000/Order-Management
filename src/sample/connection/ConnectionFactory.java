package sample.connection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private final String URL = "jdbc:mysql://localhost:3306/order_management?useSSL=false";
    private final String USER = "root";
    private final String PASSWORD = "1234";
    private final String DRIVER = "ConnectionFactory";

    private static ConnectionFactory singleInstance = new ConnectionFactory();


    public ConnectionFactory(){
        try{
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *Metoda va fi folosita pentru crearea unei conexiuni cu baza de date, folosind atributele clasei.
     * @return
     * @throws SQLException
     */
    private Connection createConnection() throws SQLException{
        Connection connection;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            return connection;
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "An error occurred while trying to connect to the database");
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnection() throws SQLException {
        return singleInstance.createConnection();
    }

    /**
     *Metoda este folosita pentru inchiderea conexiunii dupa ce s-a executat operatia dorita pe tabele.
     * @param connection
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "An error occurred while trying to close the connection");
            }
        }
    }

    /**
     *Metoda este folosita pentru a inchide un statement SQL.
     * @param statement
     */
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "An error occurred while trying to close the statement");
            }
        }
    }

    /**
     *Metoda este folosita pentru a inchide un ResultSet primit dupa efectuarea query-ului SQL.
     * @param resultSet
     */
    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "An error occurred while trying to close the ResultSet");
            }
        }
    }

}

