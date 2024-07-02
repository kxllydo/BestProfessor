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
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.time.Duration;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.*;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@RestController
@CrossOrigin(origins = "http://localhost:3000/")

public class UniversityController {

    private WebDriver driver (boolean visible){
        if (visible == false){
            ChromeOptions opt = new ChromeOptions();
            opt.addArguments("--headless=new");
            WebDriver driver = new ChromeDriver(opt);
            return driver;
        }else{
            WebDriver driver = new ChromeDriver();
            return driver;
        }
    }

    @GetMapping("/api/university-options/{university-name}")
    public Map<String, List<School>> universityOptions(@PathVariable(name = "university-name", required = true) String universityName) {
        WebDriver driver = driver(false);
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

    @GetMapping("/graph")
    public void graphql(){
        String query = "{\n" +
                "  \"query\": \"query SchoolSearchResultsPageQuery($query: SchoolSearchQuery!) { search: newSearch { ...SchoolSearchPagination_search_1ZLmLD } } fragment SchoolSearchPagination_search_1ZLmLD on newSearch { schools(query: $query, first: 8, after: \\\"\\\") { edges { cursor node { name ...SchoolCard_school id __typename } } pageInfo { hasNextPage endCursor } resultCount } } fragment SchoolCard_school on School { legacyId name numRatings avgRating avgRatingRounded ...SchoolCardHeader_school ...SchoolCardLocation_school } fragment SchoolCardHeader_school on School { name } fragment SchoolCardLocation_school on School { city state } }\",\n" +
                "  \"variables\": {\n" +
                "    \"query\": {\n" +
                "      \"text\": \"drexel\"\n" +  // Search term
                "    }\n" +
                "  }\n" +
                "}";

    String token = "Basic dGVzdDp0ZXN0";  // Replace with your actual token
    JsonObject response = fetchSchoolDetails(query, token);

    if (response != null) {
        System.out.println(response.toString());
    } else {
        System.out.println("Failed to fetch gschool details.");
    }
    }

    public static JsonObject fetchSchoolDetails(String query, String token) {
        String url = "https://www.ratemyprofessors.com/graphql";
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + token);
        
            StringEntity entity = new StringEntity(query);
            httpPost.setEntity(entity);
        
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    return JsonParser.parseString(responseBody).getAsJsonObject();
                } else {
                    System.out.println("Request failed with status code: " + response.getStatusLine().getStatusCode());
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        }
}
    

//"query SchoolSearchResultsPageQuery(\n  $query: SchoolSearchQuery!\n) {\n  search: newSearch {\n    ...SchoolSearchPagination_search_1ZLmLD\n  }\n}\n\nfragment SchoolSearchPagination_search_1ZLmLD on newSearch {\n  schools(query: $query, first: 8, after: \"\") {\n    edges {\n      cursor\n      node {\n        name\n        ...SchoolCard_school\n        id\n        __typename\n      }\n    }\n    pageInfo {\n      hasNextPage\n      endCursor\n    }\n    resultCount\n  }\n}\n\nfragment SchoolCard_school on School {\n  legacyId\n  name\n  numRatings\n  avgRating\n  avgRatingRounded\n  ...SchoolCardHeader_school\n  ...SchoolCardLocation_school\n}\n\nfragment SchoolCardHeader_school on School {\n  name\n}\n\nfragment SchoolCardLocation_school on School {\n  city\n  state\n}\n"


// document.querySelector('.css-2b097c-container').addEventListener('click', function() {
//     // Use a timeout to wait for the dropdown to render (if necessary)
//     setTimeout(function() {
//         // Get the dropdown menu div
//         const menuDiv = document.querySelector('.css-d0tfi8-menu');
//         if (menuDiv) {
//             // Get all child divs (the options) of the menu div
//             const optionDivs = menuDiv.querySelectorAll('div');
//             optionDivs.forEach((optionDiv, index) => {
//                 console.log(`Option ${index + 1}:`, optionDiv);
//                 console.log('Class list:', optionDiv.classList);
//                 console.log('Inner text:', optionDiv.innerText);
//             });
//         } else {
//             console.log('Dropdown menu not found.');
//         }
//     }, 100); // Adjust the timeout as needed based on how the menu renders
// });
