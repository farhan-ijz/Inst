package com.wurfel.yaqeen.inst.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Farhan Ijaz on 2/8/2018.
 */

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}
