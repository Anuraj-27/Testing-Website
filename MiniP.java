package Mini;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MiniP {

    // !! UPDATE THIS to your actual unzip path !!
    static final String BASE =
        "file:///C:/Users/2478731/IdeaProjects/tripadvisor-replica";

    public static void main(String[] args) throws InterruptedException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized","--disable-notifications");
        options.setExperimentalOption("excludeSwitches",new String[]{"enable-automation"});

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // ============================================================
        // MODULE 1 — HOTELS
        // Opens the replica home page, searches Nairobi,
        // sets dates + 4 guests, sorts by Traveler Rating,
        // applies Elevator filter, prints top 3 hotels.
        // ============================================================

        // Open replica home page
        driver.get(BASE + "/index.html");
        Thread.sleep(1500);

        // Type "Nairobi" in global search and press Enter
        WebElement searchBox = driver.findElement(By.id("globalSearch"));
        searchBox.sendKeys("Nairobi");
        Thread.sleep(500);
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(1500);

        // Now on hotels.html — set check-in date
        LocalDate today    = LocalDate.now();
        LocalDate checkIn  = today.plusDays(1);
        LocalDate checkOut = today.plusDays(6);
        String ciStr = checkIn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String coStr = checkOut.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        js.executeScript("document.getElementById('checkin').value='" + ciStr + "'");
        js.executeScript("document.getElementById('checkout').value='" + coStr + "'");
        Thread.sleep(500);

        // Open guest selector and set 4 adults
        driver.findElement(By.id("guestsBtn")).click();
        Thread.sleep(800);

        // Click + twice: 2 adults → 4 adults
        WebElement plusBtn = driver.findElement(By.id("adultsPlusBtn"));
        plusBtn.click(); Thread.sleep(400);
        plusBtn.click(); Thread.sleep(400);

        // Click Update button
        driver.findElement(By.id("guestsUpdateBtn")).click();
        Thread.sleep(600);

        // Click Search
        driver.findElement(By.id("searchBtn")).click();
        Thread.sleep(1500);

        // Sort by Traveler Rating
        WebElement sortSel = driver.findElement(By.id("sortSelect"));
        new Select(sortSel).selectByValue("rating");
        Thread.sleep(1200);

        // Apply Elevator filter
        WebElement elevatorCb = driver.findElement(By.id("filterElevator"));
        if (!elevatorCb.isSelected()) {
            js.executeScript("arguments[0].click();", elevatorCb);
        }
        Thread.sleep(1200);

        // Extract top 3 hotel names and prices — two separate lists
        List<WebElement> hotelNames  = driver.findElements(
                By.xpath("//div[@data-automation='hotel-card-title']"));
        List<WebElement> hotelPrices = driver.findElements(
                By.xpath("//div[@data-automation='metaRegularPrice']"));

        System.out.println("\n========== TOP 3 HOTELS IN NAIROBI ==========");
        for (int i = 0; i < 3 && i < hotelNames.size(); i++) {
            String name  = hotelNames.get(i).getText().trim();
            String price = (i < hotelPrices.size()) ? hotelPrices.get(i).getText().trim() : "N/A";
            String total = "N/A";
            try {
                long perNight = Long.parseLong(price.replaceAll("[^0-9]", ""));
                total = "₹" + String.format("%,d", perNight * 5);
            } catch (Exception ignored) {}
            System.out.println((i + 1) + ". " + name);
            System.out.println("   Price / Night : " + price);
            System.out.println("   Total (5 days): " + total);
            System.out.println("----------------------------------------------");
        }
        System.out.println("==============================================\n");

        // ============================================================
        // MODULE 2 — RESTAURANTS WITH FILTERS
        // Navigate back to home, click Restaurants,
        // apply Lunch + African + Fish checkboxes,
        // print top 3 restaurant names.
        // ============================================================

        // Go back to home page
        driver.get(BASE + "/index.html");
        Thread.sleep(1200);

        // Click Restaurants nav link
        driver.findElement(By.xpath(
                "//a[normalize-space()='Restaurants']")).click();
        Thread.sleep(1500);

        System.out.println("Landed on: " + driver.getTitle());

        // Apply Meal Type: Lunch
        applyCheckbox(driver, js, "mealLunch", "Lunch");
        Thread.sleep(1000);

        // Apply Cuisines: African
        applyCheckbox(driver, js, "cuisineAfrican", "African");
        Thread.sleep(1000);

        // Apply Dishes: Fish
        applyCheckbox(driver, js, "dishFish", "Fish");
        Thread.sleep(1500);

        // Extract top 3 restaurant names
        List<WebElement> restCards = driver.findElements(
                By.xpath("//div[@class='rest-name']"));

        System.out.println("========== TOP 3 RESTAURANTS IN NAIROBI ===========");
        System.out.println("   Filters: Meal=Lunch | Cuisine=African | Dish=Fish");
        System.out.println("----------------------------------------------------");
        int count = 0;
        for (WebElement r : restCards) {
            String name = r.getText().trim();
            if (name.isEmpty()) continue;
            count++;
            System.out.println(count + ". " + name);
            System.out.println("----------------------------------------------------");
            if (count == 3) break;
        }
        System.out.println("====================================================\n");

        // ============================================================
        // MODULE 3 — CRUISES
        // Navigate to Cruises, pick first cruise line,
        // pick first ship, extract languages list,
        // print passengers / crew / launched year.
        // ============================================================

        driver.get(BASE + "/cruises.html");
        Thread.sleep(1500);

        // Select first cruise line
        WebElement firstLine = driver.findElement(
                By.xpath("(//div[contains(@class,'cruise-line-card')])[1]"));
        String lineName = firstLine.findElement(By.className("cl-name")).getText();
        firstLine.click();
        Thread.sleep(1200);

        System.out.println("========== CRUISE LINE SELECTED ==========");
        System.out.println("Cruise Line : " + lineName);
        System.out.println("==========================================\n");

        // Select first ship
        WebElement firstShip = driver.findElement(
                By.xpath("(//div[contains(@class,'ship-card')])[1]"));
        String shipName = firstShip.findElement(By.className("ship-name")).getText();
        firstShip.click();
        Thread.sleep(1200);

        System.out.println("========== SHIP SELECTED =================");
        System.out.println("Ship : " + shipName);
        System.out.println("==========================================\n");

        // Extract Languages — stored in List
        List<WebElement> langEls = driver.findElements(
                By.xpath("//span[contains(@class,'lang-tag')]"));
        List<String> languages = new ArrayList<>();
        for (WebElement el : langEls) {
            String l = el.getText().trim();
            if (!l.isEmpty()) languages.add(l);
        }

        System.out.println("========== LANGUAGES OFFERED =============");
        System.out.println("Total : " + languages.size());
        for (int i = 0; i < languages.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + languages.get(i));
        }
        System.out.println("==========================================\n");

        // Extract ship specs
        String passengers   = driver.findElement(By.id("spec-passengers")).getText().trim();
        String crew         = driver.findElement(By.id("spec-crew")).getText().trim();
        String launchedYear = driver.findElement(By.id("spec-launched")).getText().trim();

        System.out.println("========== SHIP DETAILS ==================");
        System.out.println("Passengers   : " + passengers);
        System.out.println("Crew         : " + crew);
        System.out.println("Launched Year: " + launchedYear);
        System.out.println("==========================================\n");

        // Navigate back to home
        driver.get(BASE + "/index.html");
        Thread.sleep(1000);
        System.out.println("Navigated back to home: " + driver.getTitle());

        Thread.sleep(2000);
        driver.quit();
        System.out.println("\n✅ All 3 modules completed successfully.");
    }

    // Helper — clicks a checkbox by its element ID
    static void applyCheckbox(WebDriver driver, JavascriptExecutor js,
                               String id, String label) throws InterruptedException {
        try {
            WebElement cb = driver.findElement(By.id(id));
            js.executeScript(
                "arguments[0].scrollIntoView({behavior:'smooth',block:'center'});", cb);
            Thread.sleep(500);
            if (!cb.isSelected()) {
                // Click the label for more reliable checkbox interaction
                WebElement lbl = driver.findElement(
                        By.xpath("//label[@for='" + id + "']"));
                try { lbl.click(); }
                catch (Exception e) { js.executeScript("arguments[0].click();", cb); }
            }
            System.out.println("✅ Filter applied: " + label);
        } catch (Exception e) {
            System.out.println("⚠  Filter not found: " + label);
        }
    }
}
