package com.kxllydo.bestprofessor.controllers;
import com.kxllydo.bestprofessor.models.Professor;
import com.kxllydo.bestprofessor.models.Department;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration; 
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

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

import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@CrossOrigin(origins = "http://localhost:3000/")

public class DepartmentController {

    private Department dept = new Department();

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

    private void closeOpeningPopUp (WebDriver driver) {
        // <button class="Buttons__Button-sc-19xdot-1 CCPAModal__StyledCloseButton-sc-10x9kq-2 eAIiLw" type="button">Close</button>
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        try{
            WebElement initalPopUp = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".Buttons__Button-sc-19xdot-1.CCPAModal__StyledCloseButton-sc-10x9kq-2.eAIiLw")));
            initalPopUp.click();
        } catch (Exception e){
            try{
                WebElement popUp = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".bx-close.bx-close-link.bx-close-inside")));
                popUp.click();
            } catch (Exception x){

            }
        } 
    }

    //<a id="bx-close-inside-1177612" class="bx-close bx-close-link bx-close-inside" data-click="close" href="javascript:void(0)"><svg class="bx-close-xsvg" viewBox="240 240 20 20" aria-hidden="true"><g class="bx-close-xstroke bx-close-x-adaptive"><path class="bx-close-x-adaptive-1" d="M255.6 255.6l-11.2-11.2" vector-effect="non-scaling-stroke"></path><path class="bx-close-x-adaptive-2" d="M255.6 244.4l-11.2 11.2" vector-effect="non-scaling-stroke"></path></g></svg><div class="bx-ally-label">close dialog</div></a>


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

    
    //gets list of professors from a department filter

    // @GetMapping("/api/professors")
    // public ArrayList<Professor> getProfessors(@RequestParam(name = "school", required = true) int schoolId, @RequestParam(name = "department", required = false) String department) {
    public List<Professor> getProfessors(int schoolId, String department) {
        WebDriver driver = driver(false);
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
        dept.setProfessors(professors);
        return professors;
    }

    @GetMapping("/api/courses/{univ-id}/{dept}")
    public List<String> getCourses(@PathVariable(name = "univ-id") int univId, @PathVariable (name="dept") String dept){//Map<String, List<Professor>> getCourses(){
        WebDriver driver = driver(false);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));


        List<Professor> professors = getProfessors(univId, dept);
        Map <String, Professor> courseAndProfs = new HashMap<>();
        List<String> courses = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();

    
        for (Professor p : professors){
            int pid = p.getId();
            driver.get(String.format("https://www.ratemyprofessors.com/professor/%d", pid));
            closeOpeningPopUp(driver);

            WebElement drop = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.css-15snuh2-control")));    

            drop.click();

            try{
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.css-d0tfi8-menu")));
            }catch (Exception e){
                continue;
            }
            for (WebElement course: driver.findElements(By.cssSelector("div.css-1qsby6g-option"))){
                String name = course.getText();
                name = name.replaceAll("\\s*\\(.*$", "");
                
                if (!courses.contains(name) && (name != "All courses")){
                    courses.add(name);
                    System.out.println(name);
                }
            }
        }
        driver.quit();
        return courses;
    }

    @GetMapping("/api/departments/{univ-id}")
    public List<String> getDepartments (@PathVariable (name = "univ-id", required = true) int univId){
        WebDriver driver = driver(false);
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

        driver.quit();

        return departments;
    }
    /**
     * Helper function that extracts the tags from a professor using the professor id and link. If no tags are included on the page, it specifies that there are no notes
     * @param driver is the given driver
     * @param id is the professor id
     * @return list of the tags
     */
    public List<String> getTags(int id){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        WebDriver driver = new ChromeDriver(options);

        // int id  = 1816941;
        driver.get(String.format("https://www.ratemyprofessors.com/professor/%d", id));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        List<String> tags = new ArrayList<>();
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.TeacherTags__TagsContainer-sc-16vmh1y-0.dbxJaW")));

            for (WebElement element : driver.findElements(By.cssSelector("span.Tag-bs9vf4-0.hHOVKF"))){
                String tag = element.getText();
                if (!tags.contains(tag)){
                    tags.add(tag);
                }
            }
        }catch (Exception e){
            tags.add("No notes");
            System.out.println("error happened");
            driver.quit();
            return tags;
        }
        driver.quit();
        return tags;
    }
}
