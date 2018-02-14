package com.miv_sher.crimecat.model;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;
import java.util.UUID;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

public class Crime {
   // private UUID mId;
    public String uid;
    String author;
    public String title;
    public String date;
    public String type;
    public String sex;
    public String age;
    public String city;
    public String phone;
    public String mail;
    public String description;
    public String image;
    public String happy = "false";
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    //private boolean mSolved;

    public Crime() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Crime(String uid, String author, String title, String date,
                 String type, String sex, String age, String city, String phone,String mail,
                  String description, String image) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.date = date;
        this.type = type;
        this.sex = sex;
        this.age = age;
        this.city = city;
        this.phone = phone;
        this.mail = mail;
        this.description = description;
        this.image = image;

    }


    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("date", date);
        result.put("type", type);
        result.put("sex", sex);
        result.put("age", age);
        result.put("city", city);
        result.put("phone", phone);
        result.put("mail", mail);
        result.put("description", description);
        result.put("image", image);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("happy", happy);

        return result;
    }
    // [END post_to_map]
}