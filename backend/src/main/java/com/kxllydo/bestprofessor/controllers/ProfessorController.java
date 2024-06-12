package com.kxllydo.bestprofessor.controllers;
import com.kxllydo.bestprofessor.models.Professor;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration; 
import java.util.regex.Pattern;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@RestController
public class ProfessorController {

    /**
     * Repeatedly clicks on "Show More" of Professor Search Results page, if exists.
     * Driver must have already loaded in page.
     * 
     * @param driver WebDriver Object of Professor Search Results page. 
     */
    public void clickShowMore(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        JavascriptExecutor driverJs = (JavascriptExecutor) driver;
        int i = 0;

        WebElement button;

        try {
            button = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.Buttons__Button-sc-19xdot-1")));
        } catch (TimeoutException e) {
            System.out.println("Show More button could not be found.");
            return;
        }
        
        // WebElement button = driver.findElement(By.cssSelector("button.Buttons__Button-sc-19xdot-1"));
        Pattern buttonText = Pattern.compile("Show More");

        try {
            while (i < 2 && wait.until(ExpectedConditions.textMatches(By.cssSelector("button.Buttons__Button-sc-19xdot-1"), buttonText))) {
                i += 1;
                driverJs.executeScript("arguments[0].click();", button);
            }
        } catch (TimeoutException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/api/get-professors/{school-id}")
    public ArrayList<Professor> getProfessors(@PathVariable(name = "school-id", required = true) int schoolId) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");

        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.ratemyprofessors.com/search/professors/1521?q=*");

        try {
            long startTime = System.nanoTime() / (long) 1e9;
            clickShowMore(driver);
            long endTime = System.nanoTime() / (long) 1e9;
            System.out.println(endTime - startTime);
        } catch (Exception e) {
            driver.quit();
            return null;
        }

        ArrayList<Professor> professors = new ArrayList<>();
        for (WebElement professorNode: driver.findElements(By.cssSelector("a.TeacherCard__StyledTeacherCard-syjs0d-0"))) {
            WebElement ratingNode = professorNode.findElement(By.cssSelector("div.CardNumRating__CardNumRatingNumber-sc-17t4b9u-2"));
            float professorRating = Float.parseFloat(ratingNode.getText());
            
            if (professorRating == 0)
                continue;

            WebElement nameNode = professorNode.findElement(By.cssSelector("div.CardName__StyledCardName-sc-1gyrgim-0"));
            String professorName = nameNode.getText();

            int professorId = Integer.parseInt(professorNode.getAttribute("href").split("professor/")[1].split("/")[0]);
            
            Professor professor = new Professor(professorName, professorId, professorRating);
            professors.add(professor);
        }

        driver.quit();
        return professors;
    }

    // @GetMapping("/api/professor-options/{professor-name}")
    // public Query professorOptions(@PathVariable(name = "professor-name", required = true) String professorName) {
    //     System.setProperty("webdriver.chrome.driver", "C:\\Users\\do-kelly\\Downloads\\chromedriver.exe");
    //     WebDriver driver = new ChromeDriver();
    //     WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    //     List<String> response = new ArrayList<>();
    //     String query = "https://www.ratemyprofessors.com/search/professors/1521?q=" + professorName.replaceAll(" ", "%20");

    //     try {
    //         driver.get(query);
    //         wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".CardName__StyledCardName-sc-1gyrgim-0.cJdVEK")));
    //         List<WebElement> elements = driver.findElements(By.cssSelector(".CardName__StyledCardName-sc-1gyrgim-0.cJdVEK"));
    //         for (WebElement element : elements) {
    //             String professor = element.getText();
    //             response.add(professor);
    //         }
    //     } finally {
    //         driver.quit(); // Ensure the WebDriver is properly closed
    //     }

    //     return new Query(query, response);
    // }
}
