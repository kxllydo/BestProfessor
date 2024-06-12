package com.kxllydo.bestprofessor.controllers;
import com.kxllydo.bestprofessor.models.School;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeOptions;


@RestController
public class UniversityController {

    @GetMapping("/api/university-options/{university-name}")
    public List<School> universityOptions(@PathVariable(name = "university-name", required = true) String universityName) {
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver(opt);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        String query = "https://www.ratemyprofessors.com/search/schools?q=" + universityName.replaceAll(" ", "%20");
        List<School> options = new ArrayList<>();

        try {
            driver.get(query);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.SchoolCard__StyledSchoolCard-sc-130cnkk-0.bJboOI")));
            List<WebElement> links = driver.findElements(By.cssSelector("a.SchoolCard__StyledSchoolCard-sc-130cnkk-0.bJboOI"));            
            List<WebElement> schools = driver.findElements(By.cssSelector(".SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0.bAQoPm"));

            for (int i=0; i < links.size(); i++){
                String href = links.get(i).getAttribute ("href");
                String strId = href.replaceAll("\\D", "");
                int id = Integer.parseInt(strId);

                String name = schools.get(i).getText();
                School univ = new School(name, id);
                options.add(univ);
            }
        } finally {
            driver.quit(); 
        }
        return options;
    }
}
