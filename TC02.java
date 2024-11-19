package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

class FlightSearchV2 {
    private WebDriver driver;
    private WebDriverWait wait;
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public FlightSearchV2() {
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
        setSliderPosition(".rc-slider-handle-2", -0.25);  // 18:00 için yüzde 75
    }

    private void setSliderPosition(String sliderSelector, double offsetPercentage) {
        WebElement sliderHandle = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(sliderSelector)));
        int trackWidth = driver.findElement(By.cssSelector(".rc-slider")).getSize().getWidth();
        int xOffset = (int) (trackWidth * offsetPercentage);

        Actions moveSlider = new Actions(driver);
        moveSlider.clickAndHold(sliderHandle).moveByOffset(xOffset, 0).release().perform();
    }

    public List<Double> getTurkishAirlinesPrices() {
        List<WebElement> thyFlights = driver.findElements(By.xpath("//div[@data-testid='Türk Hava Yolları']/ancestor::div[contains(@class, 'flight-card')]"));
        List<Double> thyPrices = new ArrayList<>();

        for (WebElement flight : thyFlights) {
            WebElement priceElement = flight.findElement(By.cssSelector("div[data-testid='flightInfoPrice']"));
            String priceText = priceElement.getAttribute("data-price"); // "data-price" özniteliğinden fiyatı alın
            thyPrices.add(Double.parseDouble(priceText));
        }
        return thyPrices;
    }

    public void close() {
        driver.quit();
    }
}

class PriceVerification {
    public boolean isSorted(List<Double> prices) {
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                System.out.println("Hata: Fiyatlar artan sırada değil. Hatalı fiyat: " + prices.get(i - 1) + " ve " + prices.get(i));
                return false;
            }
        }
        return true;
    }
}

public class TC02 {

    private FlightSearchV2 flightSearch;
    private PriceVerification priceVerification;

    @BeforeMethod
    public void setUp() {
        flightSearch = new FlightSearchV2();
        priceVerification = new PriceVerification();
        flightSearch.goToWebsite();
    }

    @Test
    public void testTurkishAirlinesPriceSorting() {
        try {
            flightSearch.setDeparture("İstanbul");
            flightSearch.setArrival("Ankara");
            flightSearch.setDepartureDate("2024-11-28");
            flightSearch.setReturnDate("2024-11-28");
            flightSearch.searchFlights();

            // Zaman filtresini uygula
            flightSearch.filterDepartureTime("10:00", "18:00");

            // Türk Hava Yolları fiyatlarını al ve artan sırada olup olmadığını kontrol et
            List<Double> thyPrices = flightSearch.getTurkishAirlinesPrices();
            boolean isSorted = priceVerification.isSorted(thyPrices);

            // Test geçerse mesajı göster
            if (isSorted) {
                System.out.println("Türk Hava Yolları uçuşlarının fiyatları artan sırada.");
            }

            Assert.assertTrue(isSorted, "Test başarısız: Fiyatlar artan sırada değil.");
        } catch (Exception e) {
            Assert.fail("Test, bir hata nedeniyle başarısız oldu: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        if (flightSearch != null) {
            flightSearch.close();
        }
    }
}
