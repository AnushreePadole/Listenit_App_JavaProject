package com.example.login;

public class Users {

    private String username, password,phone;

    public Users()
    {

    }

    public Users(String username, String password, String phone) {
        this.username = username;
        this.password = password;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
