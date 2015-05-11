package com.pod.podtestapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by manuMohan on 15/05/11.
 */
public class Organization {
    @SerializedName("name")
    private String name;

    @SerializedName("org_id")
    private long orgId;

    @SerializedName("spaces")
    private ArrayList<Space> spaces;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public ArrayList<Space> getSpaces() {
        return spaces;
    }

    public void setSpaces(ArrayList<Space> spaces) {
        this.spaces = spaces;
    }
}
