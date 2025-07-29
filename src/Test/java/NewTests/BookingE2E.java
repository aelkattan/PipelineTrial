package NewTests;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.Duration;

public class BookingE2E {
    WebDriver driver;
    WebDriverWait wait;
    String checkInDate;
    String checkOutDate;
    String location;

    @BeforeTest
    public void initalization() throws IOException {

        String[] testData = DataProvider.getTestData();
        checkInDate = testData[0];     // "We 1 October 2025"
        checkOutDate = testData[1];    // "Tu 14 October 2025"
        location = testData[2];        // "Alexandria"

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.booking.com/");
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

    }

    @Test(priority = 0)
    public void full_scenario() throws InterruptedException {
        driver.findElement(By.xpath("//input[@placeholder='Where are you going?']")).sendKeys(location);//for location search
        driver.findElement(By.xpath("//button[@data-testid='searchbox-dates-container']")).click(); //for date picker
        driver.findElement(By.xpath("//button[@aria-label='Next month']")).click();//button for moving to next month
        driver.findElement(By.xpath("//button[@aria-label='Next month']")).click();
        driver.findElement(By.xpath("//span[@aria-label='" + checkInDate + "']")).click();//For check-in date selection
        driver.findElement(By.xpath("//span[@aria-label='" + checkOutDate + "']")).click();//For check-out date selection
        Thread.sleep(2000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@type='submit']")));
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        boolean hotelFound = false;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String originalWindow = driver.getWindowHandle();


        while (!hotelFound) {
            try {
                // Try to find the hotel by partial link or name
                WebElement hotelLink = driver.findElement(By.xpath("//div[@data-testid='availability-cta']/a[contains(@href,'royal-tulip-alexandria')]"));
                System.out.println("Hotel found!");
                hotelLink.click();
                hotelFound = true;
                break;
            } catch (NoSuchElementException e) {
                // If hotel is not found, scroll down
                js.executeScript("window.scrollBy(0, document.body.scrollHeight);");

                try {
                    // Click "Load more results" if visible
                    WebElement loadMoreBtn = driver.findElement(By.xpath("//button[contains(., 'Load more results')]"));

                    if (loadMoreBtn.isDisplayed() && loadMoreBtn.isEnabled()) {
                        loadMoreBtn.click();
                    }
                } catch (NoSuchElementException ex) {
                    // No load more button means possibly end of results
                    System.out.println("No more results to load.");
                }
            }
        }

        if (!hotelFound) {
            System.out.println("Hotel not found after loading all results.");
        }
        wait.until(driver -> driver.getWindowHandles().size() > 1);
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@value='2' and @name='bedPreference_78883120']")));
        WebElement bedOption = driver.findElement(By.xpath("//input[@value='2' and @name='bedPreference_78883120']"));

        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", bedOption);
        wait.until(ExpectedConditions.elementToBeClickable(bedOption));
        bedOption.click();
        driver.findElement(By.name("nr_rooms_78883120_386871369_0_33_0_131741")).sendKeys("1");
        driver.findElement(By.xpath("//span[@class='bui-button__text js-reservation-button__text']")).click();

    }
    @Test(priority = 1)
    public void Assertions(){
        String expectedCheckIn = "Check-in\nWed, Oct 1, 2025\nFrom 2:00 PM";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//time[@datetime='Wed, Oct 1, 2025']")));
        WebElement checkInDate = driver.findElement(By.xpath("//time[@datetime='Wed, Oct 1, 2025']"));
        String actualCheckIn = checkInDate.getText().trim();
        System.out.println("Actual check-in: " + actualCheckIn);
        Assert.assertEquals(actualCheckIn, expectedCheckIn, "Check-in date does not match!");
        String expectedCheckOut = "Check-out\nTue, Oct 14, 2025\nUntil 12:00 PM";
        WebElement checkOutElement = driver.findElement(By.xpath("//time[@datetime='Tue, Oct 14, 2025']"));
        String actualCheckOut = checkOutElement.getText().trim();
        System.out.println("Actual Check-out:\n" + actualCheckOut);
        Assert.assertEquals(actualCheckOut, expectedCheckOut, "Check-out does not match");
        String expectedHotelName = "Tolip Hotel Alexandria";
        WebElement HotelName = driver.findElement(By.cssSelector("h1[class='e7addce19e']"));
        String actualHotelName = HotelName.getText().trim();
        System.out.println("Actual Hotel Name: " + actualHotelName);
        Assert.assertEquals(actualHotelName, expectedHotelName, "Hotel name does not match!");
    }


    /*@AfterTest
    public void teardown() {
        driver.quit();
    }*/
}


