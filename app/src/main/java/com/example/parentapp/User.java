package com.example.parentapp;

public class User
{
    private String email;
    private String password;

    public User(String username, String password){

        this.email = username;
        this.password = password;
    }

    public String getUsername() {
        return email;
    }

    // Setter for username
    public void setUsername(String email) {
        this.email = email;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }
}
