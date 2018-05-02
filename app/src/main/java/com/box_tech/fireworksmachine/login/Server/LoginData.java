package com.box_tech.fireworksmachine.login.Server;

/**
 * Created by scc on 2018/3/15.
 * 登录后返回的数据
 */

@SuppressWarnings("unused")
public class LoginData {
    private String token;
    private long member_id;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getMember_id() {
        return member_id;
    }

    public void setMember_id(long member_id) {
        this.member_id = member_id;
    }
}
