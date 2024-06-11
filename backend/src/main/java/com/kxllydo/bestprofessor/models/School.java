package com.kxllydo.bestprofessor.models;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.By;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.support.ui.ExpectedConditions;

// import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;

public class School {
    private String name;
    private int id;

    public School (){
        this.name = "";
        this.id = 0;
    }

    public void setName (String schoolName){
        name = schoolName;
    }

    public void setId (int schoolId){
        id = schoolId;
    }

    // // public void schoolOptions(Query query, WebDriver driver, WebDriverWait wait){
    // //     String url = query.url();
    // //     System.out.println(url);
        
    //     // List<String> schools = new ArrayList<>();
    //     // try{
    //     //     driver.get(url);
    //     //     wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0.bAQoPm")));
    //     //     List<WebElement> elements = driver.findElements(By.cssSelector(".SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0.bAQoPm"));
    //     //     for (WebElement element : elements) {
    //     //         System.out.println(element.getText());
    //     //         schools.add(element.getText());
    //     //     }

    //     //     driver.quit();

        // }catch(IOException e) {
        //     e.printStackTrace();
        //     System.out.println("noo");
        // }
    }
    // String element = "<div class=\"SchoolCardHeader__StyledSchoolCardHeader-sc-1gq3qdv-0 bAQoPm\">West Chester University of Pennsylvania</div>";
    
    public static void main(String[] args) {
        // System.setProperty("webdriver.chrome.driver", "C:\\Users\\do-kelly\\Downloads\\chromedriver.exe");
        // WebDriver driver = new ChromeDriver();
        // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        // Query q = new Query ("Drexel");

    }
}