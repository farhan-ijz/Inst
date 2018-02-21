package com.wurfel.yaqeen.inst.Models;

/**
 * Created by Farhan Ijaz on 2/13/2018.
 */

public class Section {

    String name;
    int imageResourceId;
    boolean commingSoon;
    String tagId;

    public Section(String name, int resId, String tagId, Boolean commingSoon){
        this.name = name;
        this.imageResourceId = resId;
        this.commingSoon = commingSoon;
        this.tagId = tagId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommingSoon(boolean commingSoon) {
        this.commingSoon = commingSoon;
    }

    public boolean getCommingSoon() {
        return commingSoon;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagId() {
        return tagId;
    }
}
