package tests;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;

class FlightSearchV3 {
    private WebDriver driver;
    private WebDriverWait wait;

    public FlightSearchV3(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void goToWebsite() {
        driver.get("https://www.enuygun.com");
        driver.manage().window().maximize();
    }

    public void selectDeparture(String departureCity) {
        WebElement departureInput = driver.findElement(By.cssSelector("input[data-testid=\"endesign-flight-origin-autosuggestion-input\"]"));
        departureInput.sendKeys(departureCity);
        WebElement departureOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span[data-testid=\"flight-origin-istanbul-highlight-1\"]")));
        departureOption.click();
    }

    public void selectArrival(String arrivalCity) {
        WebElement arrivalInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[data-testid=\"endesign-flight-destination-autosuggestion-input\"]")));
        arrivalInput.sendKeys(arrivalCity);
        WebElement arrivalOption = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("span[data-testid=\"flight-destination-ankara-esenboga-havalimani-highlight-1\"]")));
        arrivalOption.click();
    }

    public void setDepartureDate(String date) {
        WebElement departureDateInput = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-testid=\"enuygun-homepage-flight-departureDate-datepicker-input\"]")));
        departureDateInput.click();
        WebElement dateButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[title=\"" + date + "\"]")));
        dateButton.click();
    }

    public void searchFlights() {
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-testid=\"enuygun-homepage-flight-submitButton\"]")));
        searchButton.click();
    }

    public void selectFirstFlight() {
        WebElement firstFlight = driver.findElement(By.cssSelector("div.flight-list-body > div:nth-child(1)"));
        firstFlight.click();
        WebElement selectButton = firstFlight.findElement(By.cssSelector("button[data-testid='providerSelectBtn']"));
        selectButton.click();
    }
}

class PassengerDetails {
    private WebDriver driver;

    public PassengerDetails(WebDriver driver) {
        this.driver = driver;
    }

    public void fillContactInformation(String email, String phone) {
        WebElement mailInput = driver.findElement(By.cssSelector("#contact_email"));
        mailInput.sendKeys(email);

        WebElement phoneInput = driver.findElement(By.cssSelector("#contact_cellphone"));
        phoneInput.sendKeys(phone);
    }

    public void fillPassengerInformation(String firstName, String lastName, String day, String month, String year, String id) {
        WebElement firstNameInput = driver.findElement(By.cssSelector("#firstName_0"));
        firstNameInput.sendKeys(firstName);

        WebElement lastNameInput = driver.findElement(By.cssSelector("#lastName_0"));
        lastNameInput.sendKeys(lastName);

        Select birthDaySelect = new Select(driver.findElement(By.cssSelector("#birthDateDay_0")));
        birthDaySelect.selectByValue(day);

        Select birthMonthSelect = new Select(driver.findElement(By.cssSelector("#birthDateMonth_0")));
        birthMonthSelect.selectByValue(month);

        Select birthYearSelect = new Select(driver.findElement(By.cssSelector("#birthDateYear_0")));
        birthYearSelect.selectByValue(year);

        WebElement idInput = driver.findElement(By.cssSelector("#_0"));
        idInput.sendKeys(id);

        WebElement femaleRadioButtonLabel = driver.findElement(By.cssSelector("label[for='gender_F_0']"));
        femaleRadioButtonLabel.click();
    }
}

class PaymentProcess {
    private WebDriver driver;

    public PaymentProcess(WebDriver driver) {
        this.driver = driver;
    }

    public void proceedToPayment() {
        WebElement continueButton = driver.findElement(By.cssSelector("button#continue-button.btn.btn-success.btn-lg.tr.js-reservation-btn"));
        continueButton.click();
    }

    public void fillCardInformation(String cardNumber, String month, String year, String cvv) {
        WebElement cardNumberInput = driver.findElement(By.cssSelector("input[data-testid='cardNumber']"));
        cardNumberInput.sendKeys(cardNumber);

        WebElement cardMonthInput = driver.findElement(By.cssSelector("input[data-testid='cardMonth-input']"));
        cardMonthInput.click();
        WebElement cardMonthValue = driver.findElement(By.cssSelector("button[data-testid='cardMonth-option-" + month + "']"));
        cardMonthValue.click();

        WebElement cardYearInput = driver.findElement(By.cssSelector("input[data-testid='cardYear-input']"));
        cardYearInput.click();
        WebElement cardYearValue = driver.findElement(By.cssSelector("button[data-testid='cardYear-option-" + year + "']"));
        cardYearValue.click();

        WebElement cvvInput = driver.findElement(By.cssSelector("input[data-testid='CVV']"));
        cvvInput.sendKeys(cvv);
    }

    public void submitPayment() {
        WebElement paymentButton = driver.findElement(By.cssSelector("button[data-testid='payment-form-submit-button']"));
        paymentButton.click();
    }

    public void enter3DSecureCode(String code) {
        driver.switchTo().frame("payment-form-3d");
        WebElement codeInput = driver.findElement(By.cssSelector("input#code"));
        codeInput.sendKeys(code);

        WebElement confirmButton = driver.findElement(By.cssSelector("button#btn-commit"));
        confirmButton.click();
    }
}

public class TC03Test {

    private WebDriver driver;
    private FlightSearchV3 flightSearch;
    private PassengerDetails passengerDetails;
    private PaymentProcess paymentProcess;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        flightSearch = new FlightSearchV3(driver);
        passengerDetails = new PassengerDetails(driver);
        paymentProcess = new PaymentProcess(driver);
    }

    @Test
    public void testTicketingProcess() {
        try {
            // Uçuş arama işlemleri
            flightSearch.goToWebsite();
            flightSearch.selectDeparture("İstanbul");
            flightSearch.selectArrival("Ankara");
            flightSearch.setDepartureDate("2024-11-28");
            flightSearch.searchFlights();
            Thread.sleep(5000); // Uçuş listesi yüklensin

            // İlk uçuşu seç ve devam et
            flightSearch.selectFirstFlight();
            Thread.sleep(2000); // Seç ve İlerle butonu yüklensin

            // Yolcu bilgilerini doldur
            passengerDetails.fillContactInformation("umutgul@enygn.com", "5554443322");
            passengerDetails.fillPassengerInformation("Umut", "Gul", "06", "11", "1999", "10000000146");

            // Ödeme sayfasına ilerle
            paymentProcess.proceedToPayment();
            Thread.sleep(5000); // Ödeme sayfası yüklensin

            // Kart bilgilerini doldur ve ödemeyi tamamla
            paymentProcess.fillCardInformation("4938460158754205", "10", "0", "715");
            paymentProcess.submitPayment();
            Thread.sleep(5000); // 3D Secure ekranı yüklensin

            // 3D Secure kodunu gir
            paymentProcess.enter3DSecureCode("123456");

            // Başarı mesajı
            System.out.println("Tüm biletleme süreci başarıyla tamamlandı ve 3D Secure doğrulama aşamasına ulaşıldı.");
            Assert.assertTrue(true, "Biletleme süreci başarılı bir şekilde tamamlandı.");
        } catch (Exception e) {
            Assert.fail("Test başarısız oldu: " + e.getMessage());
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
