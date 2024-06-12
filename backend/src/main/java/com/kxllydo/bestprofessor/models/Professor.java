package com.kxllydo.bestprofessor.models;
import java.util.ArrayList;
import java.util.List;

public class Professor {
    private String fullName;
    private int id;
    private int rating;
    private List<String> tags;


    public Professor (String fullName, int id, int rating){
        this.fullName = fullName;
        this.id = id;
        this.rating = rating;
    }

    public void setFullName(String name){
        fullName = name;
    }

    public void setId(int num){
        id = num;
    }

    public void setRating(int rate){
        rating = rate;
    }

    public void setTags(List<String> tag){
        tags = tag;
    }

    public String getFullName(){
        return fullName;
    }

    public int getId(){
        return id;
    }

    public int getRating(){
        return rating;
    }

    public List<String> getTags(){
        return tags;
    }

}

