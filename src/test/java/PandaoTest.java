import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PandaoTest {
    private static WebDriver driver;

    @BeforeAll
    public static void setUp() {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void tearDown() {
        driver.close();
    }

    @Test
    public void testPandao() throws InterruptedException {
        driver.get("https://pandao.ru/product/59b78009-6feb-49cf-800a-780605129c2c");
        assertTrue(driver.getTitle().startsWith("Мужской свитер с v-образным вырезом"));

        String actualImgSelector = ".product-images .fotorama .fotorama__active.fotorama__loaded--img img";

        WebElement img = driver.findElement(By.cssSelector(actualImgSelector));
        assertTrue(img.isDisplayed(), "Картинка есть, но не видна");

        String expectedImgSrc = "https://go3.imgsmail.ru/imgpreview?key=125d8301c40e1cb5&mb=storage&w=540";
        assertEquals(expectedImgSrc, img.getAttribute("src"));

        WebElement productName = driver.findElement(By.cssSelector("h1[itemprop=\"name\"]"));
        assertEquals("Мужской свитер с v-образным вырезом", productName.getText());


        List<WebElement> thumbnails = driver.findElements(By.cssSelector(".product-thumbs .thumb"));

        for (WebElement thumb: thumbnails) {
            String expectedThumbKey = getKeyParamFromUrl(thumb.findElement(By.cssSelector("img")).getAttribute("src"));

            thumb.click();
            sleep(350);

            String actualImgKey = getKeyParamFromUrl(driver.findElement(By.cssSelector(actualImgSelector)).getAttribute("src"));

            assertEquals(expectedThumbKey, actualImgKey);
        }

        driver.findElement(By.cssSelector(".btn.review-btn")).click();

        String expectedTitleElementText = "Все отзывы";
        String actualTitleElementText = driver.findElement(By.cssSelector(".main-title")).getText();
        assertEquals(expectedTitleElementText, actualTitleElementText);

        String expectedAllReviewsPageUrl = "https://pandao.ru/product/reviews/59b78009-6feb-49cf-800a-780605129c2c";
        String actualPageUrl = driver.getCurrentUrl();
        assertEquals(expectedAllReviewsPageUrl, actualPageUrl);

    }

    public String getKeyParamFromUrl(String input) {
        String result = null;

        try {
            Pattern p = Pattern.compile("(?<=key=).*?(?=&|$)");
            Matcher m = p.matcher(input);

            if (m.find()) {
                result = m.group();
            }

        } catch (PatternSyntaxException ex) {
            // error handling
        }

        return result;
    }
}
