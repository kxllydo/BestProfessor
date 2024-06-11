package com.kxllydo.bestprofessor.controllers;
import com.kxllydo.bestprofessor.models.Query;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UniversityController {


    @GetMapping("/api/match-university/{university-name}")
    public Query universityOptions(@PathVariable(name = "university-name", required = true) String universityName) {
        Document doc;
        List<String> response = new ArrayList<>();
        String query = "https://www.ratemyprofessors.com/search/schools?q=" + universityName.replaceAll(" ", "%20");

        try {
            doc = Jsoup.connect(query).get();
            Elements names = doc.select("div.SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0.bAQoPm");
            for (Element name: names){
                String school = name.text();
                response.add(school);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } 

        return new Query(query, response);
    }
}
