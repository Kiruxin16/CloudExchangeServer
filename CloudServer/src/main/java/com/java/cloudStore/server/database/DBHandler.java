package com.java.cloudStore.server.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public void getSharedList(String login,List<String> shared) throws SQLException{
        shared.add("        SHARED FILES");
        preparedStatement = connection.prepareStatement(
                "SELECT path " +
                        "FROM users "+
                        "JOIN shared_files ON shared_user_id=users.id "+
                        "WHERE users.login=?"
        );
        preparedStatement.setString(1,login);
        ResultSet rs =preparedStatement.executeQuery();
        while(rs.next()){
            shared.add(rs.getString(1));
        }

    }
    public void shareFile(String filename,String login,String path) throws SQLException{
        preparedStatement=connection.prepareStatement(
                "INSERT INTO shared_files "+
                        "(name,path,shared_user_id) "+
                        "VALUES (?,?, " +
                        "(SELECT id FROM users WHERE login =?)" +
                        ")"

        );
        preparedStatement.setString(1,filename);
        preparedStatement.setString(2,path);
        preparedStatement.setString(3,login);
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
