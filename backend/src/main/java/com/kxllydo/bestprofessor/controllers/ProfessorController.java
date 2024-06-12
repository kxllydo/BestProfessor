package com.kxllydo.bestprofessor.controllers;
import com.kxllydo.bestprofessor.models.Query;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.Duration; 

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
    public Query professorOptions(@PathVariable(name = "professor-name", required = true) String professorName) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\do-kelly\\Downloads\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        List<String> response = new ArrayList<>();
        String query = "https://www.ratemyprofessors.com/search/professors/1521?q=" + professorName.replaceAll(" ", "%20");

        try {
            driver.get(query);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".CardName__StyledCardName-sc-1gyrgim-0.cJdVEK")));
            List<WebElement> elements = driver.findElements(By.cssSelector(".CardName__StyledCardName-sc-1gyrgim-0.cJdVEK"));
            for (WebElement element : elements) {
                String professor = element.getText();
                response.add(professor);
            }
        } finally {
            driver.quit(); // Ensure the WebDriver is properly closed
        }

        return new Query(query, response);
    }
    
}
