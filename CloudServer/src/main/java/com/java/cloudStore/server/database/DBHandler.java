package com.java.cloudStore.server.database;

import java.sql.*;

public class DBHandler {

    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    public DBHandler() {

        try{
            Class.forName("org.sqlite.JDBC");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public void connect()throws Exception{
        connection=DriverManager.getConnection("jdbc:sqlite:CloudServer/UsersBase.db");
        statement = connection.createStatement();
    }

    public boolean isUserExist(String login) throws SQLException {

        ResultSet rs=statement.executeQuery("SELECT * FROM users WHERE login=\""+login+"\";");
        if(rs.next()){
            return true;
        }
        return false;
    }

    public boolean authIsCorrect(String login, String pass) throws SQLException{
        preparedStatement = connection.prepareStatement("SELECT pass FROM users WHERE login=?;");
        preparedStatement.setString(1,login);
        ResultSet rs =preparedStatement.executeQuery();
        if(rs.next()){
            return rs.getString(1).equals(pass);
        }
        return false;
    }

    public void addUser(String login, String password) throws SQLException{
        preparedStatement = connection.prepareStatement("INSERT INTO users (login,pass) VALUES (?,?)");
        preparedStatement.setString(1,login);
        preparedStatement.setString(2,password);
        preparedStatement.executeUpdate();

    }


    public  void disconnect(){
        try {

            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}
