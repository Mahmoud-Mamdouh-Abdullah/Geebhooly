package com.mahmoudkhalil.hathooly.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    String id;
    String title;
    String description;
    String categoryName;
    String userEmail;
    String userName;
    String userPhone;
    String location;
    List<String> imageUrls;
    String timeStamp;
    String key;

    public Post() {
    }

    public Post(String id, String title, String description, String categoryName, String userEmail, String userName, String userPhone, String location, List<String> imageUrls, String timeStamp, String key) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryName = categoryName;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPhone = userPhone;
        this.location = location;
        this.imageUrls = imageUrls;
        this.timeStamp = timeStamp;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public void setLocation(String location) {
        this.location = location;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
