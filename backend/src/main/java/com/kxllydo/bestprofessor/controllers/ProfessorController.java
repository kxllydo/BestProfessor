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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


@RestController
public class ProfessorController {

    @GetMapping("/api/professor-options/{professor-name}")
    public Query professorOptions(@PathVariable(name = "professor-name", required = true) String professorName, WebDriver driver, WebDriverWait wait) {
        Document doc;
        List<String> response = new ArrayList<>();
        String query = "https://www.ratemyprofessors.com/search/professors/1521?q=" + professorName.replaceAll(" ", "%20");
        
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("CardName__StyledCardName-sc-1gyrgim-0.cJdVEK")));
        List <WebElement> erm = driver.findElements(By.className("CardName__StyledCardName-sc-1gyrgim-0.cJdVEK"));
        for (WebElement x : erm){
            System.out.println(x.getText());
        }

        // try {
        //     doc = Jsoup.connect(query).get();
        //     Elements names = doc.select("div.CardName__StyledCardName-sc-1gyrgim-0.cJdVEK");
        //     System.out.println(names);

        //     for (Element name: names){
        //         String prof = name.text();
        //         System.out.println(prof);
        //         response.add(prof);
        //     }
        // } catch (IOException e) {
        //     throw new RuntimeException(e);
        // } 
        // return new Query(query, response);
    }

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\do-kelly\\Downloads\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 20);

        professorOptions("mark boady", driver, wait);


    }
}
