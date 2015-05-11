package com.pod.podtestapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by manuMohan on 15/05/11.
 */
public class Space {
    @SerializedName("name")
    private String name;

    @SerializedName("space_id")
    private long spaceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(long spaceId) {
        this.spaceId = spaceId;
    }
}
