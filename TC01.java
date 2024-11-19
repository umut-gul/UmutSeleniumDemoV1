package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.interactions.Actions;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


class FlightSearch {
    private WebDriver driver;
    private WebDriverWait wait;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public FlightSearch() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.manage().window().maximize();
    }

    public void goToWebsite() {
        driver.get("https://www.enuygun.com");
    }

    public void setDeparture(String departureCity) {
        WebElement departureInput = driver.findElement(By.cssSelector("input[data-testid=\"endesign-flight-origin-autosuggestion-input\"]"));
        departureInput.sendKeys(departureCity);
        WebElement cityOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span[data-testid=\"flight-origin-istanbul-highlight-1\"]")));
        cityOption.click();
    }

    public void setArrival(String arrivalCity) {
        WebElement arrivalInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[data-testid=\"endesign-flight-destination-autosuggestion-input\"]")));
        arrivalInput.sendKeys(arrivalCity);
        WebElement cityOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span[data-testid=\"flight-destination-ankara-esenboga-havalimani-highlight-1\"]")));
        cityOption.click();
    }

    public void setDepartureDate(String date) {
        WebElement departureDateInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-testid=\"enuygun-homepage-flight-departureDate-datepicker-input\"]")));
        departureDateInput.click();
        WebElement dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title=\"" + date + "\"]")));
        dateButton.click();
    }

    public void setReturnDate(String date) {
        WebElement returnDateInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("div[data-testid=\"enuygun-homepage-flight-returnDate-label\"]")));
        returnDateInput.click();
        WebElement dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title=\"" + date + "\"]")));
        dateButton.click();
    }

    public void searchFlights() {
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-testid=\"enuygun-homepage-flight-submitButton\"]")));
        searchButton.click();
    }

    public void filterDepartureTime(String start, String end) {
        WebElement timeFilterButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("i.ctx-filter-departure-return-time.ei-expand-more")));
        timeFilterButton.click();

        // Başlangıç ve bitiş saat kaydırıcılarını ayarla
        setSliderPosition(".rc-slider-handle-1", 0.4167); // 10:00 için yaklaşık yüzde 41.67
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        setSliderPosition(".rc-slider-handle-2", -0.25);  // 18:00 için yüzde 75
    }

    private void setSliderPosition(String sliderSelector, double offsetPercentage) {
        WebElement sliderHandle = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(sliderSelector)));
        int trackWidth = driver.findElement(By.cssSelector(".rc-slider")).getSize().getWidth();
        int xOffset = (int) (trackWidth * offsetPercentage);

        Actions moveSlider = new Actions(driver);
        moveSlider.clickAndHold(sliderHandle).moveByOffset(xOffset, 0).release().perform();
    }

    public boolean verifyFilteredTimes(String start, String end) {
        LocalTime startTime = LocalTime.parse(start, timeFormatter);
        LocalTime endTime = LocalTime.parse(end, timeFormatter);
        List<WebElement> departureTimes = driver.findElements(By.cssSelector("div[data-testid='departureTime']"));

        for (WebElement timeElement : departureTimes) {
            String timeText = timeElement.getText();
            LocalTime departureTime = LocalTime.parse(timeText, timeFormatter);

            if (departureTime.isBefore(startTime) || departureTime.isAfter(endTime)) {
                System.out.println("Hata: Uçuş saati " + timeText + " filtre aralığında değil.");
                return false;  // Testin başarısız olması
            } else {
                System.out.println("Uçuş saati " + timeText + " filtreye uygun.");
            }
        }
        return true;
    }

    public void close() {
        driver.quit();
    }
}

public class TC01 {

    private FlightSearch flightSearch;

    @BeforeMethod
    public void setUp() {
        flightSearch = new FlightSearch();
        flightSearch.goToWebsite();
    }

    @Test
    public void testFlightSearchAndFilter() {
        try {
            flightSearch.setDeparture("İstanbul");
            flightSearch.setArrival("Ankara");
            flightSearch.setDepartureDate("2024-11-28");
            flightSearch.setReturnDate("2024-11-28");
            flightSearch.searchFlights();

            // Wait for search results to load
            Thread.sleep(2000);

            // Apply filter for departure time
            flightSearch.filterDepartureTime("10:00", "18:00");

            // Wait for filter to apply
            Thread.sleep(2000);

            // Verify if all flights are within the filtered time range
            boolean testResult = flightSearch.verifyFilteredTimes("10:00", "18:00");
            Assert.assertTrue(testResult, "Test başarısız: Filtre dışında uçuş saati bulundu.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Assert.fail("Test failed due to an interruption: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        if (flightSearch != null) {
            flightSearch.close();
        }
    }
}