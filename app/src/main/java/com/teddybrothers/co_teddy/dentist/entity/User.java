package com.teddybrothers.co_teddy.dentist.entity;

/**
 * Created by co_teddy on 4/14/2017.
 */

public class User {


    public User()
    {

    }


    public User(String email, String device_token, String fullname, String status, String statusUser, String dateCreated) {
        this.email = email;
        this.device_token = device_token;
        this.fullname = fullname;
        this.status = status;
        this.statusUser = statusUser;
        this.dateCreated = dateCreated;
    }

    public String email;
    public String device_token;
    public String fullname;
    public String status;
    public String statusUser;
    public String dateCreated;

}
