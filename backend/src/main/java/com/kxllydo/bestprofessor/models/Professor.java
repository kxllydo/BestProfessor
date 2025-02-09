package com.kxllydo.bestprofessor.models;

import java.util.ArrayList;
import java.util.List;

public class Professor {
    private String fullName;
    private int id;
    private float rating;
    private List<String> tags;

    public Professor (String fullName, int id, float rating){
        this.fullName = fullName;
        this.id = id;
        this.rating = rating;
    }

    public void setTags(List<String> tag){
        if (tag == null){
            tags = new ArrayList<>();
        }else{
            tags = tag;
        }
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public String getFullName(){
        return fullName;
    }

    public int getId(){
        return id;
    }

    public float getRating(){
        return rating;
    }

    public List<String> getTags(){
        return tags;
    }
}

