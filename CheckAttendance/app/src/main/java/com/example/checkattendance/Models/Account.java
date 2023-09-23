package com.example.checkattendance.Models;

public class Account {
    private String username ="";
    private String password ="";
    private String position ="";

    public Account(String username, String password, String position) {
        this.username = username;
        this.password = password;
        this.position = position;
    }
    public int AuthenticateLogin(String user  , String pass)
    {
        if (user.equals(this.username))
        {
            if(pass.equals(this.password))
            {
                if(!this.position.equals("BOSS"))
                {
                    return 3;//vai trò nhân viên
                }else
                {
                    return 4; // Vai trò quản lý
                }
            }else
            {
                return 2;// sai password.
            }
        }
        return 1;//khong ton tai username trong database.
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}