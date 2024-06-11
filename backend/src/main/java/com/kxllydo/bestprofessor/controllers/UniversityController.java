package com.kxllydo.bestprofessor.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

@RestController
public class UniversityController {

    @GetMapping("/api/match-university/{university-name}")
    public String matchUniversity(@PathVariable(name = "university-name", required = true) String universityName) {
        Document doc;
        String name;
        try {
            System.out.println("----------------------------------------------------------------------------------------------------------------");
            doc = Jsoup.connect("https://www.ratemyprofessors.com/search/schools?q=" + universityName.replaceAll(" ", "%20")).get();
            Elements el = doc.select("div.SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0.bAQoPm"); //("a.SchoolCard__StyledSchoolCard-sc-130cnkk-0 div.SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0");
            for (Element hi: el){
                name = el.text();
                System.out.println(name);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return universityName;
    }
}
