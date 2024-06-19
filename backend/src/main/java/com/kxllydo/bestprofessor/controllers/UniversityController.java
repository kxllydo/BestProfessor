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
import java.util.HashMap;
import java.util.Map;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.*;

import org.springframework.web.bind.annotation.CrossOrigin;



@RestController
@CrossOrigin(origins = "http://localhost:3000/") // Allow all origins for this controller

public class UniversityController {

    @GetMapping("/api/university-options/{university-name}")
    public Map<String, List<School>> universityOptions(@PathVariable(name = "university-name", required = true) String universityName) {
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

        Map<String, List<School>> universities = new HashMap<>();
        universities.put("options", options);

        return universities;
    }

    @GetMapping("/api")
    public List<String> dropdown (){
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        List<String> departments = new ArrayList<>();

        driver.get("https://www.ratemyprofessors.com/search/professors/1521?q=*");

        WebElement popup = driver.findElement(By.cssSelector(".ReactModal__Overlay.ReactModal__Overlay--after-open.FullPageModal__StyledFullPageModal-sc-1tziext-1.fyxlwo__overlay"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].classList.remove('ReactModal__Overlay', 'ReactModal__Overlay--after-open', 'FullPageModal__StyledFullPageModal-sc-1tziext-1', 'fyxlwo__overlay')", popup);        

        WebElement drop = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".css-1l6bn5c-control")));
        drop.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".css-1u8e7rt-menu")));
        List<WebElement> depts = driver.findElements(By.cssSelector(".css-l0mlil-option"));

        for (WebElement dep : depts){
            String name = dep.getText();
            departments.add(name);
            System.out.println(name);
        }

        return departments;
    }
}
