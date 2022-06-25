package com.java.cloudStore.server.database;

import com.java.cloudStore.api.AuthResponse;
import com.java.cloudStore.api.RegResponse;
import com.java.cloudStore.api.ShareResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public List<String> getSharedList(String login){
        List<String> shared =new ArrayList<>();
        try {
            dbHandler.connect();
            dbHandler.getSharedList(login,shared);
            if (shared.size()<=1){
                shared.clear();
            }
            return shared;

        }catch (Exception e){
            e.printStackTrace();
            return shared;
        }finally {
            dbHandler.disconnect();
        }
    }

    public ShareResponse sharingFiles(String name, String login, String path){
        try{
            dbHandler.connect();
            dbHandler.shareFile(name,login,path);
            return new ShareResponse(true,"");
        }catch (Exception e){
            e.printStackTrace();
            return new ShareResponse(false,"cannot share");
        }finally {
            dbHandler.disconnect();
        }


    }


}
