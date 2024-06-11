package com.kxllydo.bestprofessor.models;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;


@Component
public class Query {
    private String query;
    private List<String> response;
    
    public Query(String query, List<String> response){
        this.query = query;
        this.response = response;
    }

    public void printUrl(){
        System.out.println(query);
    }

    public void printResponse(){
        for (String school : response){
            System.out.println(school);
        }
    }

    public String getUrl(){
        return query;
    }

    public List<String> getResponse() {
        return response;
    }


}