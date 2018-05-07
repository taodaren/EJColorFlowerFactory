package com.box_tech.fireworksmachine.login;

public class LoginSession {
    private String username;
    private String password;
    private long member_id;
    private String token;

    public LoginSession(String username, String password, long member_id, String token){
        this.username = username;
        this.password = password;
        this.member_id = member_id;
        this.token = token;
    }

    public LoginSession(String username, String password){
        this.username = username;
        this.password = password;
        this.member_id = 0;
        this.token = null;
    }

    public long getMember_id() {
        return member_id;
    }

    public void setMember_id(long member_id) {
        this.member_id = member_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
}
