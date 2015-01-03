package net.mancke.microcart.osiam;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "ageMinutes" })
public class AuthCookie {
    private String displayName;
    private String userName;
    private ArrayList<String> groups;
    private long lastSeen;
    private String userId;

    public AuthCookie() {
    }

    public int getAgeMinutes() {
        return (int) ((System.currentTimeMillis() - lastSeen) / ((long) 60 * 1000));
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void addGrpup(String value) {
        groups.add(value);
    }
}
