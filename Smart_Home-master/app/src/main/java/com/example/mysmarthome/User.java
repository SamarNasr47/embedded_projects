package com.example.mysmarthome;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String userId; // New field for user ID
    private String name;
    private String username;
    private String password;
    private String email;
    private String birthdate;
    private String bestfriend;
    private List<String> logEntries;
    private boolean fanStatus;
    private String profileImageUrl; // New field for profile image URL

    public User() {
        // Default constructor required for Firebase
    }

    public User(String userId, String name, String username, String password, String email, String birthdate, String besti) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthdate = birthdate;
        this.bestfriend = besti;
        this.logEntries = new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getBestfriend() {
        return bestfriend;
    }

    public void setBestfriend(String bestfriend) {
        this.bestfriend = bestfriend;
    }

    public List<String> getLogEntries() {
        return logEntries;
    }

    public void setLogEntries(List<String> logEntries) {
        this.logEntries = logEntries;
    }

    public void addLog(String log) {
        logEntries.add(log);
    }

    public List<String> getLogs() {
        return logEntries;
    }

    public boolean isFanStatus() {
        return fanStatus;
    }

    public void setFanStatus(boolean fanStatus) {
        this.fanStatus = fanStatus;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", bestfriend='" + bestfriend + '\'' +
                ", logEntries=" + logEntries +
                ", fanStatus=" + fanStatus +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}