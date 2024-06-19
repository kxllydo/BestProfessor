package com.kxllydo.bestprofessor.models;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private List<String> courses;
    private List<Professor> professors;

    public Department (){
        courses = new ArrayList<>();
        professors = new ArrayList<>();
    }

    public void setCourses(List<String> classes){
        courses = classes;
    }

    public void setProfessors(List<Professor> profs){
        professors = profs;
    }

    public List<String> getCourses(){
        return courses;
    }

    public List<Professor> getProfessors(){
        return professors;
    }
}
