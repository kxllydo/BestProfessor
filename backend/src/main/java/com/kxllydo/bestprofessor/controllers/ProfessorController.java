package com.kxllydo.bestprofessor.controllers;
import com.kxllydo.bestprofessor.models.Professor;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration; 
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.ElementClickInterceptedException;

@RestController
public class ProfessorController {

    /**
     * Attempts to click the element, and will remove any obscuring elements in the way.
     * Assumption that element is ready to be clicked.
     * 
     * @param driver
     * @param element
     */
    public void attemptToClick(WebDriver driver, WebElement element) {
        JavascriptExecutor driverJs = (JavascriptExecutor) driver;
        Actions action = new Actions(driver);

        try {
            action.scrollToElement(element)
                  .moveToElement(element)
                  .perform();
            element.click();
        } catch (ElementClickInterceptedException e) {
            try { Thread.sleep(250); } catch (Exception e2) {}

            List<WebElement> hoverElements = (List<WebElement>) driverJs.executeScript("return document.querySelectorAll(':hover');");
            WebElement hoverElement;

            if (hoverElements.size() > 0)
                hoverElement = hoverElements.get(hoverElements.size() - 1);
            else {
                driver.quit();
                throw new RuntimeException(e);
            }

            driverJs.executeScript("arguments[0].remove();", hoverElement);
            attemptToClick(driver, element);
        } catch (Exception e) {
            driver.quit();
            throw new RuntimeException(e);
        }
    }


    /**
     * Opens the department filter dropdown and selects the given department, if present.
     * 
     * @param driver
     * @param department
     */
    public void clickDepartmentFilter(WebDriver driver, String department) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
    
        // Clicks on dropdown menu
        WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".css-1l6bn5c-control")));
        attemptToClick(driver, dropdown);

        // Gathers all department names
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.css-1u8e7rt-menu")));
        List<WebElement> departmentElements = driver.findElements(By.cssSelector("div.css-1u8e7rt-menu div div"));
        List<String> departmentNames = departmentElements.stream().map(element -> element.getText().toLowerCase()).toList();
        
        // Searches for department argument in list of department names & clicks
        int index = departmentNames.indexOf(department);
        if (index < 0) {
            driver.quit();
            throw new RuntimeException("Could not find department: " + department + ".");
        } else {
            WebElement departmentElement = departmentElements.get(index);
            attemptToClick(driver, departmentElement);

            // Waits for reload?
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".css-1l6bn5c-control")));
            } catch (Exception e) {
                driver.quit();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Repeatedly clicks on "Show More" of Professor Search Results page, if exists.
     * Driver must have already loaded in page.
     * 
     * @param driver WebDriver Object of Professor Search Results page. 
     */
    public void clickShowMore(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        JavascriptExecutor driverJs = (JavascriptExecutor) driver;

        WebElement button;

        try {
            button = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.Buttons__Button-sc-19xdot-1")));
        } catch (TimeoutException e) {
            return;
        }
        
        // WebElement button = driver.findElement(By.cssSelector("button.Buttons__Button-sc-19xdot-1"));
        Pattern buttonText = Pattern.compile("Show More");

        try {
            while (wait.until(ExpectedConditions.textMatches(By.cssSelector("button.Buttons__Button-sc-19xdot-1"), buttonText))) {
                driverJs.executeScript("arguments[0].click();", button);
            }
        } catch (TimeoutException e) {
            return;
        } catch (Exception e) {
            driver.quit();
            throw new RuntimeException(e);
        }
    }


    @GetMapping("/api/professors")
    public ArrayList<Professor> getProfessors(@RequestParam(name = "school", required = true) int schoolId, @RequestParam(name = "department", required = false) String department) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");

        WebDriver driver = new ChromeDriver(options);
        driver.get(String.format("https://www.ratemyprofessors.com/search/professors/%d?q=*", schoolId));

        try {
            if (department != null) {
                department = department.toLowerCase();
                clickDepartmentFilter(driver, department);
            }
            clickShowMore(driver);
        } catch (Exception e) {
            driver.quit();
            throw new RuntimeException(e);
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
}
