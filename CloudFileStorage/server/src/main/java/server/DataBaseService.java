package server;

import java.sql.*;

public class DataBaseService {
    private static Statement statement;
    private static Connection connection;


    public static void connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:CloudFileStorage.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect() throws SQLException {
        connection.close();
    }

    public static void registerNewUser(String login, int password) throws SQLException{
        String sql = String.format("insert into users (user_login, user_password) VALUES ('%s','%d')", login, password);
        statement.execute(sql);
    }

    public static boolean checkAuthorization(String login, int password){
        String sql = String.format("select * from users where user_login = '%s' and user_password = '%d'", login, password);

        try {
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
