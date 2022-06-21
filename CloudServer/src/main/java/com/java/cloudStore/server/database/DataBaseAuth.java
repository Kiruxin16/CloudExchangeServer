package com.java.cloudStore.server.database;

import com.java.cloudStore.api.AuthResponse;
import com.java.cloudStore.api.RegResponse;

import java.sql.SQLException;

public class DataBaseAuth {

    private DBHandler dbHandler;


    public DataBaseAuth() {
        this.dbHandler = new DBHandler();
    }

    public AuthResponse tryToAuth(String login,String pass)  {
        try {
            dbHandler.connect();
            if (!dbHandler.isUserExist(login)) {
                return new AuthResponse(false, "Login not found");
            } else if (!dbHandler.authIsCorrect(login, pass)) {
                return new AuthResponse(false, "Password is incorrect");
            } else {
                return new AuthResponse(true, "success");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new AuthResponse(false,"error");
        }finally {
            dbHandler.disconnect();
        }
    }

    public RegResponse tryToReg(String login,String pass) throws Exception {

        try {
            dbHandler.connect();
            if (dbHandler.isUserExist(login)) {
                return new RegResponse(false, "This name is already in usage");
            } else {
                dbHandler.addUser(login, pass);
                return new RegResponse(true, "Registration has done");
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new RegResponse(false,"error");
        }

    }


}
